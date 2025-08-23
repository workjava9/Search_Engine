package searchengine.services.indexing;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import searchengine.config.Messages;
import searchengine.config.Site;
import searchengine.config.SiteList;
import searchengine.dto.Response;
import searchengine.dto.indexing.IndexingErrorResponse;
import searchengine.dto.indexing.IndexingResponse;
import searchengine.model.*;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;
import searchengine.utills.parsing.sitemapping.SiteParser;
import searchengine.utills.parsing.sitemapping.Utils;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IndexingServiceImpl implements IndexingService {

    private ThreadPoolExecutor executor;
    private final SiteParser siteParser;
    private final SiteList siteListFromConfig;
    private final PageRepository pageRepository;
    private final SiteRepository siteRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final List<SiteEntity> siteEntityList = new ArrayList<>();

    @PersistenceContext
    private EntityManager em;

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size:50}")
    private final int batchSize;

    @Override
    @Transactional
    public Response startIndexing() {
        SiteParser.setCancel(false);
        if (isIndexingSuccessful()) {
            IndexingResponse ok = new IndexingResponse();
            ok.setResult(true);
            return ok;
        }
        IndexingErrorResponse err = new IndexingErrorResponse();
        err.setResult(false);
        err.setError(Messages.INDEXING_HAS_ALREADY_STARTED);
        return err;
    }

    @Transactional
    public boolean isIndexingSuccessful() {
        int running = siteListFromConfig.getSites().stream()
                .map(e -> siteRepository.countByNameAndStatus(e.getName(), Status.INDEXING))
                .reduce(0, Integer::sum);
        if (running > 0) return false;

        siteParser.clearUniqueLinks();

        final ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("site-indexer-%d")
                .build();
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(
                Math.max(1, Math.min(Runtime.getRuntime().availableProcessors(), 4)), threadFactory);

        siteListFromConfig.getSites().forEach(cfg -> {
            boolean isCreate = !siteRepository.existsByName(cfg.getName());
            if (SiteParser.isCancel()) {
                executor.shutdownNow();
            } else {
                executor.execute(() -> parseOneSite(cfg.getUrl(), cfg.getName(), isCreate));
            }
        });

        executor.shutdown();
        return true;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void parseOneSite(String url, String name, boolean isCreate) {
        if (SiteParser.isCancel()) return;

        SiteEntity siteEntity;
        if (isCreate) {
            siteEntity = new SiteEntity(Status.INDEXING, Utils.setNow(), url, name);
            log.info("<<<=== Site '{}' added", name);
        } else {
            siteEntity = siteRepository.findByName(name).orElse(null);
            if (siteEntity == null) {
                log.warn("Site '{}' not found", name);
                return;
            }
            siteEntity.setStatus(Status.INDEXING);
            log.info("<<<=== Site '{}' changed", siteEntity.getName());
            deleteByName(name);
        }

        siteEntity = siteRepository.save(siteEntity);
        siteEntityList.add(siteEntity);

        siteParser.initSiteParser(siteEntity.getSiteId(), Utils.getProtocolAndDomain(url), url);
        siteParser.getLinks();
    }

    void deleteByName(String name) {
        Optional<SiteEntity> siteByName = siteRepository.findByName(name);
        if (siteByName.isEmpty()) return;

        int siteId = siteByName.get().getSiteId();

        log.warn("lemma deleteAllBySiteId: {}", siteId);
        try {
            lemmaRepository.deleteAllBySiteId(siteId);
            em.flush(); em.clear();
        } catch (Exception e) {
            log.error("lemmaRepository.deleteAllBySiteId() message: {}", e.getMessage());
        }

        log.warn("page deleteAllBySiteId: {}", siteId);
        try {
            pageRepository.deleteAllBySiteId(siteId);
            em.flush(); em.clear();
        } catch (Exception e) {
            log.error("pageRepository.deleteAllBySiteId() message: {}", e.getMessage());
        }
    }

    @Override
    @Transactional
    public Response stopIndexing() {
        if (!isStopIndexing()) {
            IndexingErrorResponse err = new IndexingErrorResponse();
            err.setResult(false);
            err.setError(Messages.INDEXING_IS_NOT_RUNNING);
            return err;
        }
        IndexingResponse ok = new IndexingResponse();
        ok.setResult(true);
        return ok;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected boolean isStopIndexing() {
        long size = siteEntityList.stream().filter(e -> e.getStatus() == Status.INDEXING).count();
        if (size == 0) {
            log.warn(Messages.INDEXING_IS_NOT_RUNNING);
            return false;
        }
        siteParser.forceStop();

        siteEntityList.stream()
                .filter(e -> e.getStatus() == Status.INDEXING)
                .forEach(e -> {
                    e.setStatus(Status.FAILED);
                    e.setStatusTime(new Timestamp(System.currentTimeMillis()));
                    e.setLastError(Messages.INDEXING_STOPPED_BY_USER);
                });

        batchMerge(siteEntityList);
        log.warn(Messages.INDEXING_STOPPED_BY_USER);
        return true;
    }

    @Override
    @Transactional
    public Response indexPage(String url) {
        SiteParser.setCancel(false);
        String domain = Utils.getProtocolAndDomain(url);

        boolean belongsToConfig = siteListFromConfig.getSites().stream()
                .anyMatch(site -> site.getUrl().equals(domain));
        if (!belongsToConfig) {
            IndexingErrorResponse err = new IndexingErrorResponse();
            err.setResult(false);
            err.setError(Messages.THIS_PAGE_IS_LOCATED_OUTSIDE_THE_SITES_SPECIFIED_IN_THE_CONFIGURATION_FILE);
            return err;
        }

        Site siteCfg = siteListFromConfig.getSites().stream()
                .filter(s -> s.getUrl().equals(domain))
                .findFirst()
                .orElse(null);
        if (siteCfg == null) {
            IndexingErrorResponse err = new IndexingErrorResponse();
            err.setResult(false);
            err.setError(Messages.THIS_PAGE_IS_LOCATED_OUTSIDE_THE_SITES_SPECIFIED_IN_THE_CONFIGURATION_FILE);
            return err;
        }

        String name = siteCfg.getName();
        SiteEntity siteEntity = siteRepository.findByName(name).orElse(null);
        if (siteEntity == null) {
            siteEntity = new SiteEntity(Status.INDEXING, Utils.setNow(), domain, name);
        } else {
            siteEntity.setStatus(Status.INDEXING);
            siteEntity.setStatusTime(Utils.setNow());
            String path = url.substring(domain.length());
            deletePage(siteEntity.getSiteId(), path);
        }
        siteEntity.setLastError("");
        siteRepository.save(siteEntity);

        if (!saveLemmasAndIndicesForOnePage(url, siteEntity, domain)) {
            IndexingErrorResponse err = new IndexingErrorResponse();
            err.setResult(false);
            err.setError(Messages.THIS_PAGE_IS_LOCATED_OUTSIDE_THE_SITES_SPECIFIED_IN_THE_CONFIGURATION_FILE);
            return err;
        }

        siteEntity.setStatus(Status.INDEXED);
        siteEntity.setStatusTime(Utils.setNow());
        siteRepository.save(siteEntity);
        em.flush(); em.clear();

        log.info("page saved...");
        IndexingResponse ok = new IndexingResponse();
        ok.setResult(true);
        return ok;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected boolean saveLemmasAndIndicesForOnePage(String url, SiteEntity siteEntity, String domain) {
        Page page;
        try {
            page = siteParser.savePage(url, siteEntity, domain);
        } catch (Exception e) {
            log.warn("siteParser.savePage - error");
            return false;
        }
        if (page == null) return false;

        siteParser.parseSinglePage(page);
        em.flush(); em.clear();
        return true;
    }

    @Transactional
    protected void deletePage(int siteId, String path) {
        log.info("The page {} by siteId: {} is deleted", path, siteId);
        Page page = pageRepository.findBySiteIdAndPath(siteId, path);
        if (page != null) {
            deleteLemmas(page, siteId);
            pageRepository.delete(page);
            em.flush(); em.clear();
        }
    }

    @Transactional
    protected void deleteLemmas(Page page, int siteId) {
        List<IndexEntity> indexList = indexRepository.findByPageId(page.getPageId());
        List<Lemma> lemmaList = new ArrayList<>(indexList.size());

        for (IndexEntity idx : indexList) {
            Lemma lemma = idx.getLemmaByLemmaId();
            if (lemma != null) {
                lemma.setFrequency(lemma.getFrequency() - 1);
                lemmaList.add(lemma);
            }
        }

        batchMerge(lemmaList);

        log.info("Lemmas by pageId: {} are updated/removed", page.getPageId());
        lemmaRepository.deleteBySiteIdAndFrequency(siteId, 0);
        em.flush(); em.clear();
    }

    private <T> void batchMerge(List<T> entities) {
        if (entities == null || entities.isEmpty()) return;
        int i = 0;
        for (T entity : entities) {
            em.merge(entity);
            i++;
            if (i % batchSize == 0) {
                em.flush(); em.clear();
            }
        }
        em.flush(); em.clear();
    }
}

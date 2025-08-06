package searchengine.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import searchengine.model.SiteEntity;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SiteRepositoryTest {

    @Autowired
    private SiteRepository siteRepository;

    @Test
    void testSaveAndFind() {
        SiteEntity site = new SiteEntity();
        site.setName("Test Site");
        site.setUrl("https://example.com");
        site.setStatusTime((Timestamp) new Date());

        siteRepository.save(site);

        Optional<SiteEntity> saved = siteRepository.findByName("Test Site");
        assertTrue(saved.isPresent());
    }
}

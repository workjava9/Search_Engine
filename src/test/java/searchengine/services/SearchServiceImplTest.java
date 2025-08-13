package searchengine.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import searchengine.config.SiteList;
import searchengine.dto.Response;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;
import searchengine.services.search.RelevanceCalculator;
import searchengine.services.search.SearchServiceImpl;
import searchengine.services.search.SnippetFormatter;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class SearchServiceImplTest {

    @Mock
    private SiteList siteList;

    @Mock
    private SiteRepository siteRepository;

    @Mock
    private PageRepository pageRepository;

    @Mock
    private LemmaRepository lemmaRepository;

    @Mock
    private SnippetFormatter snippetFormatter;

    @Mock
    private RelevanceCalculator relevanceCalculator;

    @InjectMocks
    private SearchServiceImpl searchService;

    @Test
    void testSearchReturnsResponseObject() {
        Response response = searchService.search("тест", null, 0, 10);
        assertNotNull(response);
    }
}

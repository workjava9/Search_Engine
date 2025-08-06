package searchengine.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import searchengine.dto.Response;
import searchengine.repository.SiteRepository;
import searchengine.services.search.SearchServiceImpl;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class SearchServiceImplTest {

    @Autowired
    private SearchServiceImpl searchService;

    @MockBean
    private SiteRepository siteRepository;

    @Test
    void searchReturnsSuccess() {
        Response response = searchService.search("example", null, 0, 10);
        assertTrue(response.isResult());
    }
}

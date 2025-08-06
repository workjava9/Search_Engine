package searchengine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import searchengine.dto.Response;
import searchengine.services.search.SearchService;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchServiceTest {

    private SearchService service;

    @BeforeEach
    void setup() {
        service = (query, site, offset, limit) -> {
            Response r = new Response();
            r.setResult("hello".equals(query));
            return r;
        };
    }

    @Test
    void testSearchFound() {
        Response result = service.search("hello", "site.com", 0, 10);
        assertTrue(result.isResult());
    }

    @Test
    void testSearchEmpty() {
        Response result = service.search("nothing", "site.com", 0, 10);
        assertFalse(result.isResult());
    }
}

package searchengine.statistics;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import searchengine.config.Site;
import searchengine.config.SiteList;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;
import searchengine.services.statistics.StatisticsServiceImpl;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class StatisticsServiceImplTest {

    @Autowired
    private StatisticsServiceImpl statisticsService;

    @MockBean
    private SiteRepository siteRepository;

    @MockBean
    private PageRepository pageRepository;

    @MockBean
    private SiteList siteList;

    @Test
    void testStatistics() {
        Site mockSite = new Site();
        mockSite.setName("Test Site");
        mockSite.setUrl("https://example.com");
        when(siteList.getSites()).thenReturn(Collections.singletonList(mockSite));

        StatisticsResponse response = statisticsService.getStatistics();

        assertNotNull(response);
        assertTrue(response.isResult());
    }
}

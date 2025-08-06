package searchengine.statistics;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.statistics.StatisticsServiceImpl;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StatisticsServiceImplTest {

    @Autowired
    private StatisticsServiceImpl statisticsService;

    @Test
    void testStatistics() {
        StatisticsResponse response = statisticsService.getStatistics();
        assertNotNull(response);
        assertTrue(response.isResult());
    }
}

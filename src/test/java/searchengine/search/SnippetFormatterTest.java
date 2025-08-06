package searchengine.search;

import org.junit.jupiter.api.Test;
import searchengine.services.search.SnippetFormatter;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class SnippetFormatterTest {

    @Test
    void testSnippetGeneration() {
        SnippetFormatter formatter = new SnippetFormatter();
        String content = "This is a test content that contains the word Java in the middle.";
        String snippet = formatter.getSnippet(content, List.of());

        assertTrue(snippet.toLowerCase().contains("java"));
    }
}

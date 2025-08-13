//package searchengine.search;
//
//import org.junit.jupiter.api.Test;
//import searchengine.config.Site;
//import searchengine.model.Lemma;
//import searchengine.services.search.SnippetFormatter;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//class SnippetFormatterTest {
//
//    @Test
//    void testSnippetGeneration() {
//        SnippetFormatter formatter = new SnippetFormatter();
//        String content = "This is a test content that contains the word Java in the middle.";
//
//        Lemma lemma = new Lemma();
//        lemma.setLemma("java");
//        lemma.setFrequency(1); // нужный минимум
//
//        String snippet = formatter.getSnippet(content, List.of(lemma));
//
//        System.out.println("Generated snippet = " + snippet);
//        assertTrue(snippet.toLowerCase().contains("java"), "Snippet should contain the word 'java'");
//
//
//    }
//}

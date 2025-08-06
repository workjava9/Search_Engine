//package searchengine.controllers;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.test.web.servlet.MockMvc;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(SearchController.class)
//class SearchControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Test
//    void testSearchEndpoint() throws Exception {
//        mockMvc.perform(get("/api/search")
//                        .param("query", "example"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json"));
//    }
//}

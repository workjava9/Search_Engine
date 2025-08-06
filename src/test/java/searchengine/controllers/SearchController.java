//package searchengine.controllers;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import searchengine.dto.Response;
//import searchengine.services.search.SearchService;
//
//@RestController
//@RequestMapping("/api")
//class SearchController {
//
//    private final SearchService searchService;
//
//    public SearchController(SearchService searchService) {
//        this.searchService = searchService;
//    }
//
//    @GetMapping("/search")
//    public ResponseEntity<Response> search(
//            @RequestParam String query,
//            @RequestParam(required = false) String site,
//            @RequestParam(defaultValue = "0") int offset,
//            @RequestParam(defaultValue = "10") int limit) {
//        Response response = searchService.search(query, site, offset, limit);
//        return ResponseEntity.ok(response);
//    }
//}

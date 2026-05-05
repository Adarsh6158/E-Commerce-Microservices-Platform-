package com.ecommerce.search_service.Controller;

import com.ecommerce.search_service.DTO.DTOMapper;
import com.ecommerce.search_service.DTO.ProductSearchResult;
import com.ecommerce.search_service.Service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/search")
public class SearchController {
    
    private static final Logger log = LoggerFactory.getLogger(SearchController.class);
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/products")
    public Flux<ProductSearchResult> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return searchService.search(q, page, Math.min(size, 100))
                .map(DTOMapper::toDto);
    }

    @GetMapping("/products/brand/{brand}")
    public Flux<ProductSearchResult> searchByBrand(
            @PathVariable String brand,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return searchService.searchByBrand(brand, page, Math.min(size, 100))
                .map(DTOMapper::toDto);
    }

    @GetMapping("/products/category/{categoryId}")
    public Flux<ProductSearchResult> searchByCategory(
            @PathVariable String categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return searchService.searchByCategory(categoryId, page, Math.min(size, 100))
                .map(DTOMapper::toDto);
    }

    @GetMapping("/products/price")
    public Flux<ProductSearchResult> searchByPriceRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return searchService.searchByPriceRange(min, max, page, Math.min(size, 100))
                .map(DTOMapper::toDto);
    }

    @GetMapping("/products/filter")
    public Flux<ProductSearchResult> filterProducts(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return searchService.filterProducts(q, brand, categoryId, minPrice, maxPrice, page, Math.min(size, 100))
                .map(DTOMapper::toDto);
    }

        @GetMapping("/suggest")
        public Flux<Map<String, String>> suggest(@RequestParam String q) {
        if (q == null || q.trim().length() < 1) return Flux.empty();

        return searchService.suggest(q.trim())
                .map(doc -> Map.of(
                        "id", doc.getId(),
                        "name", doc.getName(),
                        "brand", doc.getBrand() != null ? doc.getBrand() : ""
                ))
                .onErrorResume(e -> {
                        log.error("Suggest failed for q='{}' | Error type: {} | Message: {}",
                                q, e.getClass().getSimpleName(), e.getMessage());
                        return Flux.empty();
                });
        }

    @GetMapping("/recommendations")
    public Flux<ProductSearchResult> recommendations(
            @RequestParam(defaultValue = "8") int size) {
        return searchService.getRecommendations(Math.min(size, 20))
                .map(DTOMapper::toDto);
    }

    @PostMapping("/reindex")
    public Mono<Map<String, Object>> reindex() {
        return searchService.reindexAll()
                .map(count -> Map.<String, Object>of(
                        "status", "completed",
                        "indexed", count
                ));
    }
}

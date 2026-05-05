package com.ecommerce.search_service.Service;

import com.ecommerce.search_service.Domain.ProductDocument;
import com.ecommerce.search_service.Repository.ProductSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchService.class);

    private final ProductSearchRepository searchRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final WebClient webClient;

    @Value("${catalog.host:localhost}")
    private String catalogHost;

    @Value("${catalog.port:8082}")
    private int catalogPort;

    public SearchService(ProductSearchRepository searchRepository,
                         ElasticsearchOperations elasticsearchOperations,
                         WebClient.Builder webClientBuilder) {
        this.searchRepository = searchRepository;
        this.elasticsearchOperations = elasticsearchOperations;
        this.webClient = webClientBuilder.build();
    }

    public Flux<ProductDocument> search(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return Mono.fromCallable(() -> searchRepository
                        .findByNameContainingOrDescriptionContaining(query, query, pageable))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .doOnSubscribe(s ->
                        log.debug("Searching products: query={}, page={}, size={}", query, page, size));
    }

    public Flux<ProductDocument> searchByBrand(String brand, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return Mono.fromCallable(() -> searchRepository.findByBrandAndActiveTrue(brand, pageable))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable);
    }

    public Flux<ProductDocument> searchByCategory(String categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return Mono.fromCallable(() -> searchRepository
                        .findByCategoryIdAndActiveTrue(categoryId, pageable))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable);
    }

    public Flux<ProductDocument> searchByPriceRange(BigDecimal minPrice,
                                                    BigDecimal maxPrice,
                                                    int page,
                                                    int size) {
        Pageable pageable = PageRequest.of(page, size);
        return Mono.fromCallable(() -> searchRepository
                        .findByBasePriceBetweenAndActiveTrue(minPrice, maxPrice, pageable))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable);
    }

    public Mono<ProductDocument> indexProduct(ProductDocument document) {
        return Mono.fromCallable(() -> searchRepository.save(document))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(d ->
                        log.info("Product indexed: id={}", d.getId()));
    }

    public Mono<Void> deleteProduct(String productId) {
        return Mono.fromRunnable(() -> searchRepository.deleteById(productId))
                .subscribeOn(Schedulers.boundedElastic())
                .then()
                .doOnSuccess(v ->
                        log.info("Product removed from index: id={}", productId));
    }

    public Flux<ProductDocument> filterProducts(String q,
                                                String brand,
                                                String categoryId,
                                                BigDecimal minPrice,
                                                BigDecimal maxPrice,
                                                int page,
                                                int size) {

        return Mono.fromCallable(() -> {

                    BoolQuery.Builder boolBuilder = new BoolQuery.Builder();
                    boolBuilder.filter(f -> f.term(t -> t.field("active").value(true)));

                    if (q != null && !q.isBlank()) {
                        boolBuilder.must(m -> m.multiMatch(mm -> mm
                                .query(q)
                                .fields("name^3", "description", "brand^2")
                                .type(co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType.BestFields)
                                .fuzziness("AUTO")));
                    }

                    if (brand != null && !brand.isBlank()) {
                        boolBuilder.filter(f -> f.term(t -> t.field("brand").value(brand)));
                    }

                    if (categoryId != null && !categoryId.isBlank()) {
                        boolBuilder.filter(f -> f.term(t -> t.field("categoryId").value(categoryId)));
                    }

                    if (minPrice != null || maxPrice != null) {
                        boolBuilder.filter(f -> f.range(r -> {
                            r.field("basePrice");
                            if (minPrice != null)
                                r.gte(co.elastic.clients.json.JsonData.of(minPrice.doubleValue()));
                            if (maxPrice != null)
                                r.lte(co.elastic.clients.json.JsonData.of(maxPrice.doubleValue()));
                            return r;
                        }));
                    }

                    NativeQuery nativeQuery = NativeQuery.builder()
                            .withQuery(Query.of(qb -> qb.bool(boolBuilder.build())))
                            .withPageable(PageRequest.of(page, size))
                            .build();

                    return elasticsearchOperations.search(nativeQuery, ProductDocument.class)
                            .getSearchHits().stream()
                            .map(SearchHit::getContent)
                            .collect(Collectors.toList());

                }).subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable);
    }

public Flux<ProductDocument> suggest(String prefix) {
    if (prefix == null || prefix.isBlank()) {
        return Flux.empty();
    }

    return Mono.fromCallable(() -> {

        NativeQuery nativeQuery = NativeQuery.builder()
            .withQuery(q -> q.bool(b -> b
                .filter(f -> f.term(t -> t.field("active").value(true)))
                .must(m -> m.multiMatch(mm -> mm
                    .query(prefix)
                    .type(TextQueryType.BoolPrefix)
                    .fields("name", "name._2gram", "name._3gram")
                ))
            ))
            .withPageable(PageRequest.of(0, 8))
            .build();

        return elasticsearchOperations
            .search(nativeQuery, ProductDocument.class)
            .getSearchHits().stream()
            .map(SearchHit::getContent)
            .collect(Collectors.toList());

    })
    .subscribeOn(Schedulers.boundedElastic())
    .flatMapMany(Flux::fromIterable)
    .doOnSubscribe(s -> log.debug("Suggest: prefix={}", prefix));
}

    public Flux<ProductDocument> getRecommendations(int size) {
        Pageable pageable = PageRequest.of(0, size);
        return Mono.fromCallable(() -> searchRepository.findByActiveTrue(pageable))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .doOnSubscribe(s ->
                        log.debug("Fetching {} recommendations", size));
    }

    public Mono<Long> reindexAll() {
        log.info("Starting full reindex from catalog service…");

        return Mono.fromCallable(() -> {
                    searchRepository.deleteAll();
                    return 0L;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then(fetchAndIndexFromCatalog());
    }

    private Mono<Long> fetchAndIndexFromCatalog() {
        return webClient.get()
                .uri("http://{host}:{port}/products", catalogHost, catalogPort)
                .retrieve()
                .bodyToFlux(ProductDocument.class)
                .flatMap(this::indexProduct)
                .count()
                .doOnSuccess(count ->
                        log.info("Reindex completed. {} products indexed.", count))
                .doOnError(e ->
                        log.error("Reindex failed", e));
    }
}

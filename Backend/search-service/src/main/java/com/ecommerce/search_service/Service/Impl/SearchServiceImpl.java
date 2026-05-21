package com.ecommerce.search_service.Service.Impl;

import com.ecommerce.search_service.Config.SearchProperties;
import com.ecommerce.search_service.Domain.ProductDocument;
import com.ecommerce.search_service.Repository.ProductSearchRepository;
import com.ecommerce.search_service.Service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import static com.ecommerce.search_service.Constant.SearchConstants.*;

@Service
public class SearchServiceImpl implements SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchServiceImpl.class);

    private final ProductSearchRepository searchRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final SearchProperties searchProperties;

    public SearchServiceImpl(ProductSearchRepository searchRepository,
                             ElasticsearchOperations elasticsearchOperations,
                             SearchProperties searchProperties) {
        this.searchRepository = searchRepository;
        this.elasticsearchOperations = elasticsearchOperations;
        this.searchProperties = searchProperties;
    }

    @Override
    public Flux<ProductDocument> search(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return Mono.fromCallable(() -> searchRepository
                        .findByNameContainingOrDescriptionContaining(query, query, pageable))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .doOnSubscribe(s ->
                        log.debug("Searching products: query={}, page={}, size={}", query, page, size));
    }

    @Override
    public Flux<ProductDocument> searchByBrand(String brand, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return Mono.fromCallable(() -> searchRepository.findByBrandAndActiveTrue(brand, pageable))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Flux<ProductDocument> searchByCategory(String categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return Mono.fromCallable(() -> searchRepository
                        .findByCategoryIdAndActiveTrue(categoryId, pageable))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable);
    }

    @Override
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

    @Override
    public Flux<ProductDocument> filterProducts(String q,
                                                String brand,
                                                String categoryId,
                                                BigDecimal minPrice,
                                                BigDecimal maxPrice,
                                                int page,
                                                int size) {
        return Mono.fromCallable(() -> {
                    BoolQuery.Builder boolBuilder = new BoolQuery.Builder();
                    boolBuilder.filter(f -> f.term(t -> t.field(FIELD_ACTIVE).value(true)));

                    if (q != null && !q.isBlank()) {
                        boolBuilder.must(m -> m.multiMatch(mm -> mm
                                .query(q)
                                .fields(FIELD_NAME_BOOSTED, FIELD_DESCRIPTION, FIELD_BRAND_BOOSTED)
                                .type(co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType.BestFields)
                                .fuzziness(FUZZINESS_AUTO)));
                    }

                    if (brand != null && !brand.isBlank()) {
                        boolBuilder.filter(f -> f.term(t -> t.field(FIELD_BRAND).value(brand)));
                    }

                    if (categoryId != null && !categoryId.isBlank()) {
                        boolBuilder.filter(f -> f.term(t -> t.field(FIELD_CATEGORY_ID).value(categoryId)));
                    }

                    if (minPrice != null || maxPrice != null) {
                        boolBuilder.filter(f -> f.range(r -> {
                            r.field(FIELD_BASE_PRICE);
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

    @Override
    public Flux<ProductDocument> suggest(String prefix) {
        if (prefix == null || prefix.isBlank()) {
            return Flux.empty();
        }

        return Mono.fromCallable(() -> {
                    NativeQuery nativeQuery = NativeQuery.builder()
                            .withQuery(q -> q.bool(b -> b
                                    .filter(f -> f.term(t -> t.field(FIELD_ACTIVE).value(true)))
                                    .must(m -> m.multiMatch(mm -> mm
                                            .query(prefix)
                                            .type(TextQueryType.BoolPrefix)
                                            .fields(FIELD_NAME, FIELD_NAME_2GRAM, FIELD_NAME_3GRAM)
                                    ))
                            ))
                            .withPageable(PageRequest.of(0, searchProperties.getSuggestLimit()))
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

    @Override
    public Flux<ProductDocument> getRecommendations(int size) {
        Pageable pageable = PageRequest.of(0, size);
        return Mono.fromCallable(() -> searchRepository.findByActiveTrue(pageable))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .doOnSubscribe(s ->
                        log.debug("Fetching {} recommendations", size));
    }
}

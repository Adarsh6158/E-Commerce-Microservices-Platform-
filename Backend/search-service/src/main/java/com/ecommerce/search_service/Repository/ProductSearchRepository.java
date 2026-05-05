package com.ecommerce.search_service.Repository;

import com.ecommerce.search_service.Domain.ProductDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, String> {

    List<ProductDocument> findByNameContainingOrDescriptionContaining(String name, String description, Pageable pageable);

    List<ProductDocument> findByBrandAndActiveTrue(String brand, Pageable pageable);

    List<ProductDocument> findByCategoryIdAndActiveTrue(String categoryId, Pageable pageable);

    List<ProductDocument> findByBasePriceBetweenAndActiveTrue(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    List<ProductDocument> findByActiveTrue(Pageable pageable);

    @Query("{\"bool\": {\"must\": [{\"bool\": {\"should\": [{\"match_phrase_prefix\": {\"name\": {\"query\": \"?0\", \"max_expansions\": 20}}}, {\"match_phrase_prefix\": {\"brand\": {\"query\": \"?0\", \"max_expansions\": 20}}}, {\"wildcard\": {\"name\": {\"value\": \"*?0*\", \"case_insensitive\": true}}}], \"minimum_should_match\": 1}}], \"filter\": [{\"term\": {\"active\": true}}]}}")
    List<ProductDocument> findSuggestions(String prefix, Pageable pageable);
}
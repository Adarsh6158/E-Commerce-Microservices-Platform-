package com.ecommerce.catalog_service.Repository;

import com.ecommerce.catalog_service.Domain.Category;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CategoryRepository extends ReactiveMongoRepository<Category, String> {

    Mono<Category> findBySlug(String slug);

    Flux<Category> findByParentId(String parentId);

    Flux<Category> findByActiveTrue();
}

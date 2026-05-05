package com.ecommerce.catalog_service.Repository;

import com.ecommerce.catalog_service.Domain.Review;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReviewRepository extends ReactiveMongoRepository<Review, String> {

    Flux<Review> findByProductIdOrderByCreatedAtDesc(String productId);

    Mono<Review> findByProductIdAndUserId(String productId, String userId);

    Mono<Long> countByProductId(String productId);
}

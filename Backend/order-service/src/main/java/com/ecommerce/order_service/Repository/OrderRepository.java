package com.ecommerce.order_service.Repository;

import com.ecommerce.order_service.Domain.Order;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface OrderRepository extends ReactiveMongoRepository<Order, String> {

    Flux<Order> findByUserIdOrderByCreatedAtDesc(String userId);

    Flux<Order> findByStatus(String status);
}
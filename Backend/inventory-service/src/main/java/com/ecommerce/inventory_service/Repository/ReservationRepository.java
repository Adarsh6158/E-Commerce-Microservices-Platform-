package com.ecommerce.inventory_service.Repository;

import com.ecommerce.inventory_service.Domain.Reservation;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ReservationRepository extends R2dbcRepository<Reservation, UUID> {

    Mono<Reservation> findByOrderIdAndProductId(String orderId, String productId);

    Flux<Reservation> findByOrderId(String orderId);

    Mono<Boolean> existsByOrderIdAndProductId(String orderId, String productId);
}

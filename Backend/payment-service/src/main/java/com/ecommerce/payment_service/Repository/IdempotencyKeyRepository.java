package com.ecommerce.payment_service.Repository;

import com.ecommerce.payment_service.Domain.IdempotencyKey;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IdempotencyKeyRepository extends R2dbcRepository<IdempotencyKey, UUID> {

    Mono<IdempotencyKey> findByIdempotencyKey(String idempotencyKey);

    Mono<IdempotencyKey> findByOrderId(String orderId);
}

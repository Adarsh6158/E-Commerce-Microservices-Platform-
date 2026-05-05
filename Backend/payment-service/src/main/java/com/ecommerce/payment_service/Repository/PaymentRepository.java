package com.ecommerce.payment_service.Repository;

import com.ecommerce.payment_service.Domain.Payment;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PaymentRepository extends R2dbcRepository<Payment, UUID> {

    Mono<Payment> findByOrderId(String orderId);

    Flux<Payment> findByUserId(String userId);

    Mono<Payment> findByTransactionRef(String transactionRef);
}

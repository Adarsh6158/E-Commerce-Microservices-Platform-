package com.ecommerce.payment_service.Service;

import com.ecommerce.payment_service.Domain.IdempotencyKey;
import com.ecommerce.payment_service.Domain.Payment;
import com.ecommerce.payment_service.Repository.IdempotencyKeyRepository;
import com.ecommerce.payment_service.Repository.PaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final PaymentRepository paymentRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TransactionalOperator transactionalOperator;
    private final ObjectMapper objectMapper;

    public PaymentService(PaymentRepository paymentRepository,
                          IdempotencyKeyRepository idempotencyKeyRepository,
                          KafkaTemplate<String, Object> kafkaTemplate,
                          TransactionalOperator transactionalOperator,
                          ObjectMapper objectMapper) {
        this.paymentRepository = paymentRepository;
        this.idempotencyKeyRepository = idempotencyKeyRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.transactionalOperator = transactionalOperator;
        this.objectMapper = objectMapper;
    }

    /**
     * Process payment with idempotency guarantee.
     * If the same orderId has already been processed, returns the cached result.
     */
    public Mono<Payment> processPayment(String orderId, String userId, BigDecimal amount, String currency) {
        String idempotencyKeyValue = "payment:" + orderId;

        return idempotencyKeyRepository.findByIdempotencyKey(idempotencyKeyValue)
                .flatMap(existingKey -> {
                    log.info("Idempotent request detected for orderId={}, returning cached result", orderId);
                    return paymentRepository.findByOrderId(orderId);
                })
                .switchIfEmpty(executePayment(orderId, userId, amount, currency, idempotencyKeyValue));
    }

    private Mono<Payment> executePayment(String orderId, String userId, BigDecimal amount,
                                         String currency, String idempotencyKeyValue) {

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setUserId(userId);
        payment.setAmount(amount);
        payment.setCurrency(currency != null ? currency : "USD");
        payment.setStatus(Payment.PaymentStatus.PROCESSING);
        payment.setPaymentMethod(Payment.PaymentMethod.CREDIT_CARD);
        payment.setTransactionRef("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        return paymentRepository.save(payment)
                .flatMap(this::simulatePaymentGateway)
                .flatMap(processedPayment -> {
                    IdempotencyKey key = new IdempotencyKey();
                    key.setIdempotencyKey(idempotencyKeyValue);
                    key.setOrderId(orderId);
                    key.setResponseStatus(
                            processedPayment.getStatus() == Payment.PaymentStatus.COMPLETED ? 200 : 422
                    );
                    key.setCreatedAt(LocalDateTime.now());
                    key.setExpiresAt(LocalDateTime.now().plusHours(24));

                    try {
                        key.setResponseBody(objectMapper.writeValueAsString(processedPayment));
                    } catch (JsonProcessingException e) {
                        key.setResponseBody("{}");
                    }

                    return idempotencyKeyRepository.save(key)
                            .thenReturn(processedPayment);
                })
                .as(transactionalOperator::transactional)
                .doOnSuccess(this::publishPaymentResult);
    }


    private Mono<Payment> simulatePaymentGateway(Payment payment) {
        return Mono.fromCallable(() -> {
            Thread.sleep(100 + RANDOM.nextInt(200));

            boolean success = RANDOM.nextInt(100) < 90;

            if (success) {
                payment.setStatus(Payment.PaymentStatus.COMPLETED);
                log.info("Payment succeeded: orderId={}, txnRef={}",
                        payment.getOrderId(), payment.getTransactionRef());
            } else {
                payment.setStatus(Payment.PaymentStatus.FAILED);
                payment.setFailureReason("Gateway declined: insufficient funds (simulated)");
                log.warn("Payment failed: orderId={}, txnRef={}",
                        payment.getOrderId(), payment.getTransactionRef());
            }

            payment.setUpdatedAt(LocalDateTime.now());
            return payment;
        }).flatMap(paymentRepository::save);
    }

    public Mono<Payment> refundPayment(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .filter(p -> p.getStatus() == Payment.PaymentStatus.COMPLETED)
                .flatMap(payment -> {
                    payment.setStatus(Payment.PaymentStatus.REFUNDED);
                    payment.setUpdatedAt(LocalDateTime.now());
                    return paymentRepository.save(payment);
                })
                .doOnSuccess(payment -> {
                    if (payment != null) {
                        log.info("Payment refunded: orderId={}, txnRef={}",
                                payment.getOrderId(), payment.getTransactionRef());
                        kafkaTemplate.send("payment.refunded", orderId, Map.of(
                                "orderId", orderId,
                                "transactionRef", payment.getTransactionRef(),
                                "amount", payment.getAmount().toString()
                        ));
                    }
                });
    }

    public Mono<Payment> getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    public Flux<Payment> getPaymentsByUserId(String userId) {
        return paymentRepository.findByUserId(userId);
    }

    private void publishPaymentResult(Payment payment) {
        if (payment == null) return;

        String topic = payment.getStatus() == Payment.PaymentStatus.COMPLETED
                ? "payment.completed"
                : "payment.failed";

        Map<String, String> event = Map.of(
                "orderId", payment.getOrderId(),
                "paymentId", payment.getId().toString(),
                "transactionRef", payment.getTransactionRef(),
                "status", payment.getStatus().name(),
                "amount", payment.getAmount().toString(),
                "reason", payment.getFailureReason() != null ? payment.getFailureReason() : ""
        );

        kafkaTemplate.send(topic, payment.getOrderId(), event);
        log.info("Published {} event for orderId={}", topic, payment.getOrderId());
    }
}

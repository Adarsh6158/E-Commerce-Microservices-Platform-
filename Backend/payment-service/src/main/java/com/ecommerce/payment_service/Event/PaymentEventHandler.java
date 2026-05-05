package com.ecommerce.payment_service.Event;

import com.ecommerce.payment_service.Service.PaymentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentEventHandler {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventHandler.class);
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    public PaymentEventHandler(PaymentService paymentService, ObjectMapper objectMapper) {
        this.paymentService = paymentService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "order.payment-requested", groupId = "payment-service-saga")
    public void handlePaymentRequested(@Payload String message,
                                       @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                       Acknowledgment ack) {
        try {
            JsonNode payload = objectMapper.readTree(message);
            String orderId = payload.get("orderId").asText();
            String userId = payload.get("userId").asText();
            BigDecimal amount = new BigDecimal(payload.get("totalAmount").asText());

            log.info("Received payment request for orderId={}, amount={}", orderId, amount);

            paymentService.processPayment(orderId, userId, amount, "USD")
                    .doOnSuccess(payment -> {
                        log.info("Payment processed for orderId={}, status={}", orderId, payment.getStatus());
                        ack.acknowledge();
                    })
                    .doOnError(e -> {
                        log.error("Error processing payment for orderId={}: {}", orderId, e.getMessage(), e);
                        ack.acknowledge();
                    })
                    .subscribe();

        } catch (Exception e) {
            log.error("Failed to parse payment request: {}", e.getMessage(), e);
            ack.acknowledge();
        }
    }

    @KafkaListener(topics = "order.cancelled", groupId = "payment-service-saga")
    public void handleOrderCancelled(@Payload String message,
                                     @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                     Acknowledgment ack) {
        try {
            JsonNode payload = objectMapper.readTree(message);
            String orderId = payload.get("orderId").asText();

            log.info("Received order cancellation, processing refund for orderId={}", orderId);

            paymentService.refundPayment(orderId)
                    .doOnSuccess(payment -> {
                        if (payment != null) {
                            log.info("Refund processed for orderId={}", orderId);
                        } else {
                            log.info("No completed payment found for refund: orderId={}", orderId);
                        }
                        ack.acknowledge();
                    })
                    .doOnError(e -> {
                        log.error("Error processing refund for orderId={}: {}", orderId, e.getMessage(), e);
                        ack.acknowledge();
                    })
                    .subscribe();

        } catch (Exception e) {
            log.error("Failed to parse order.cancelled event: {}", e.getMessage(), e);
            ack.acknowledge();
        }
    }
}

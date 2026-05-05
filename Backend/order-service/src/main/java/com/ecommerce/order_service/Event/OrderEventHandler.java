package com.ecommerce.order_service.Event;

import com.ecommerce.order_service.Service.OrderService;
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

@Component
public class OrderEventHandler {

    private static final Logger log = LoggerFactory.getLogger(OrderEventHandler.class);
    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    public OrderEventHandler(OrderService orderService, ObjectMapper objectMapper) {
        this.orderService = orderService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "inventory.reserved", groupId = "order-service-saga")
    public void handleInventoryReserved(@Payload String message,
                                        @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                        Acknowledgment ack) {
        try {
            JsonNode payload = objectMapper.readTree(message);
            String orderId = payload.get("orderId").asText();
            log.info("Received inventory.reserved for orderId={}", orderId);

            orderService.handleInventoryReserved(orderId)
                    .doOnSuccess(order -> ack.acknowledge())
                    .doOnError(e -> log.error("Error handling inventory.reserved: {}", e.getMessage(), e))
                    .subscribe();
        } catch (Exception e) {
            log.error("Failed to process inventory.reserved event: {}", e.getMessage(), e);
            ack.acknowledge();
        }
    }

    @KafkaListener(topics = "inventory.reserve-failed", groupId = "order-service-saga")
    public void handleInventoryReserveFailed(@Payload String message,
                                             @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                             Acknowledgment ack) {
        try {
            JsonNode payload = objectMapper.readTree(message);
            String orderId = payload.get("orderId").asText();
            String reason = payload.has("reason") ? payload.get("reason").asText() : "Unknown";
            log.info("Received inventory.reserve-failed for orderId={}, reason={}", orderId, reason);

            orderService.handleInventoryReserveFailed(orderId, reason)
                    .doOnSuccess(order -> ack.acknowledge())
                    .doOnError(e -> log.error("Error handling inventory.reserve-failed: {}", e.getMessage(), e))
                    .subscribe();
        } catch (Exception e) {
            log.error("Failed to process inventory.reserve-failed event: {}", e.getMessage(), e);
            ack.acknowledge();
        }
    }

    @KafkaListener(topics = "payment.completed", groupId = "order-service-saga")
    public void handlePaymentCompleted(@Payload String message,
                                       @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                       Acknowledgment ack) {
        try {
            JsonNode payload = objectMapper.readTree(message);
            String orderId = payload.get("orderId").asText();
            log.info("Received payment.completed for orderId={}", orderId);

            orderService.handlePaymentCompleted(orderId)
                    .doOnSuccess(order -> ack.acknowledge())
                    .doOnError(e -> log.error("Error handling payment.completed: {}", e.getMessage(), e))
                    .subscribe();
        } catch (Exception e) {
            log.error("Failed to process payment.completed event: {}", e.getMessage(), e);
            ack.acknowledge();
        }
    }

    @KafkaListener(topics = "payment.failed", groupId = "order-service-saga")
    public void handlePaymentFailed(@Payload String message,
                                    @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                    Acknowledgment ack) {
        try {
            JsonNode payload = objectMapper.readTree(message);
            String orderId = payload.get("orderId").asText();
            String reason = payload.has("reason") ? payload.get("reason").asText() : "Unknown";
            log.info("Received payment.failed for orderId={}, reason={}", orderId, reason);

            orderService.handlePaymentFailed(orderId, reason)
                    .doOnSuccess(order -> ack.acknowledge())
                    .doOnError(e -> log.error("Error handling payment.failed: {}", e.getMessage(), e))
                    .subscribe();
        } catch (Exception e) {
            log.error("Failed to process payment.failed event: {}", e.getMessage(), e);
            ack.acknowledge();
        }
    }
}
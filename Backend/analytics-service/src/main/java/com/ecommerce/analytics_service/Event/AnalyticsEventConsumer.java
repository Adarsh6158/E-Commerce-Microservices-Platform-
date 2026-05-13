package com.ecommerce.analytics_service.Event;

import com.ecommerce.analytics_service.Service.AnalyticsService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AnalyticsEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(AnalyticsEventConsumer.class);
    private final AnalyticsService analyticsService;
    private final ObjectMapper objectMapper;

    public AnalyticsEventConsumer(AnalyticsService analyticsService, ObjectMapper objectMapper) {
        this.analyticsService = analyticsService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "order.created", groupId = "analytics-service")
    public void consumeOrderCreated(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            Map<String, Object> event = objectMapper.readValue(record.value(), new TypeReference<>() {});
            double amount = Double.parseDouble(event.getOrDefault("totalAmount", "0").toString());
            String orderId = event.getOrDefault("orderId", record.key()).toString();
            String userId = event.getOrDefault("userId", "").toString();
            
            analyticsService.trackEvent("ORDER_CREATED", orderId, userId, event).subscribe();
            analyticsService.incrementMetric("TOTAL_ORDERS", 1).subscribe();
            analyticsService.incrementMetric("TOTAL_REVENUE", amount).subscribe();
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing order.created", e);
            ack.acknowledge();
        }
    }

    @KafkaListener(topics = "order.cancelled", groupId = "analytics-service")
    public void consumeOrderCancelled(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            Map<String, Object> event = objectMapper.readValue(record.value(), new TypeReference<>() {});
            double amount = Double.parseDouble(event.getOrDefault("totalAmount", "0").toString());
            String orderId = event.getOrDefault("orderId", record.key()).toString();
            String userId = event.getOrDefault("userId", "").toString();
            
            analyticsService.trackEvent("ORDER_CANCELLED", orderId, userId, event).subscribe();
            analyticsService.incrementMetric("CANCELLED_ORDERS", 1).subscribe();
            analyticsService.incrementMetric("TOTAL_REVENUE", -amount).subscribe();
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing order.cancelled", e);
            ack.acknowledge();
        }
    }

    @KafkaListener(topics = "payment.completed", groupId = "analytics-service")
    public void consumePaymentCompleted(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            Map<String, Object> event = objectMapper.readValue(record.value(), new TypeReference<>() {});
            String orderId = event.getOrDefault("orderId", record.key()).toString();
            
            analyticsService.trackEvent("PAYMENT_COMPLETED", orderId, "", event).subscribe();
            analyticsService.incrementMetric("COMPLETED_PAYMENTS", 1).subscribe();
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing payment.completed", e);
            ack.acknowledge();
        }
    }

    @KafkaListener(topics = "payment.refunded", groupId = "analytics-service")
    public void consumePaymentRefunded(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            Map<String, Object> event = objectMapper.readValue(record.value(), new TypeReference<>() {});
            String orderId = event.getOrDefault("orderId", record.key()).toString();
            
            analyticsService.trackEvent("PAYMENT_REFUNDED", orderId, "", event).subscribe();
            analyticsService.incrementMetric("REFUNDED_PAYMENTS", 1).subscribe();
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing payment.refunded", e);
            ack.acknowledge();
        }
    }
}

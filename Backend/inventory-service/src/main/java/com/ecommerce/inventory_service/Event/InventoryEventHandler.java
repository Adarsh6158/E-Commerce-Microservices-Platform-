package com.ecommerce.inventory_service.Event;

import com.ecommerce.inventory_service.Service.InventoryService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class InventoryEventHandler {

    private static final Logger log = LoggerFactory.getLogger(InventoryEventHandler.class);

    private final InventoryService inventoryService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public InventoryEventHandler(InventoryService inventoryService,
                                 KafkaTemplate<String, String> kafkaTemplate,
                                 ObjectMapper objectMapper) {
        this.inventoryService = inventoryService;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "order.created", groupId = "inventory-service-saga")
    public void onOrderCreated(@Payload String payload,
                               @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            JsonNode node = objectMapper.readTree(payload);
            String orderId = node.get("orderId").asText();
            String correlationId = node.has("correlationId")
                    ? node.get("correlationId").asText()
                    : "unknown";

            log.info("Processing order.created for inventory reservation. orderId={}, correlationId={}",
                    orderId, correlationId);

            JsonNode items = node.get("items");
            if (items != null && items.isArray()) {
                for (JsonNode item : items) {
                    String productId = item.get("productId").asText();
                    String sku = item.has("sku") ? item.get("sku").asText() : "";
                    int quantity = item.get("quantity").asInt();

                    inventoryService.reserveStock(orderId, productId, sku, quantity)
                            .doOnSuccess(r -> {
                                publishEvent(
                                        "inventory.reserved",
                                        orderId,
                                        String.format(
                                                "{\"orderId\":\"%s\",\"productId\":\"%s\",\"quantity\":%d,\"correlationId\":\"%s\"}",
                                                orderId, productId, quantity, correlationId
                                        ),
                                        correlationId
                                );
                            })
                            .doOnError(e -> {
                                log.error("Inventory reservation failed. orderId={}, productId={}",
                                        orderId, productId, e);
                                publishEvent(
                                        "inventory.reserve-failed",
                                        orderId,
                                        String.format(
                                                "{\"orderId\":\"%s\",\"productId\":\"%s\",\"reason\":\"%s\",\"correlationId\":\"%s\"}",
                                                orderId, productId, e.getMessage(), correlationId
                                        ),
                                        correlationId
                                );
                            })
                            .subscribe();
                }
            }
        } catch (Exception e) {
            log.error("Failed to process order.created event", e);
        }
    }

    @KafkaListener(topics = "order.cancelled", groupId = "inventory-service-saga")
    public void onOrderCancelled(@Payload String payload,
                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            JsonNode node = objectMapper.readTree(payload);
            String orderId = node.get("orderId").asText();
            String correlationId = node.has("correlationId")
                    ? node.get("correlationId").asText()
                    : "unknown";

            log.info("Processing order.cancelled – releasing stock. orderId={}, correlationId={}",
                    orderId, correlationId);

            inventoryService.releaseStock(orderId)
                    .doOnSuccess(v -> {
                        publishEvent(
                                "inventory.released",
                                orderId,
                                String.format(
                                        "{\"orderId\":\"%s\",\"correlationId\":\"%s\"}",
                                        orderId, correlationId
                                ),
                                correlationId
                        );
                    })
                    .subscribe();

        } catch (Exception e) {
            log.error("Failed to process order.cancelled event", e);
        }
    }

    @KafkaListener(topics = "order.confirmed", groupId = "inventory-service-saga")
    public void onOrderConfirmed(@Payload String payload) {
        try {
            JsonNode node = objectMapper.readTree(payload);
            String orderId = node.get("orderId").asText();

            inventoryService.confirmReservation(orderId)
                    .doOnSuccess(v ->
                            log.info("Reservation confirmed for order. orderId={}", orderId))
                    .subscribe();

        } catch (Exception e) {
            log.error("Failed to process order.confirmed event", e);
        }
    }

    private void publishEvent(String topic, String key, String payload, String correlationId) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, payload);
        record.headers().add(
                new RecordHeader("correlationId", correlationId.getBytes(StandardCharsets.UTF_8))
        );

        kafkaTemplate.send(record)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish event to {}. key={}", topic, key, ex);
                    } else {
                        log.info("Event published. topic={}, key={}", topic, key);
                    }
                });
    }
}

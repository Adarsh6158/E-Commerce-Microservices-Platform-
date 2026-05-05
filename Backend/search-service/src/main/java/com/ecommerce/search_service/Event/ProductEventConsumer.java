package com.ecommerce.search_service.Event;

import com.ecommerce.search_service.Domain.ProductDocument;
import com.ecommerce.search_service.Service.SearchService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

@Component
public class ProductEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ProductEventConsumer.class);

    private final SearchService searchService;
    private final ObjectMapper objectMapper;

    public ProductEventConsumer(SearchService searchService, ObjectMapper objectMapper) {
        this.searchService = searchService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = {"catalog.product.created", "catalog.product.updated"},
            groupId = "search-service-indexer"
    )
    public void onProductUpsert(@Payload String payload,
                                @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        try {
            JsonNode node = objectMapper.readTree(payload);
            String eventType = node.get("eventType").asText();
            String productId = node.get("productId").asText();

            ProductDocument doc = new ProductDocument();
            doc.setId(productId);
            doc.setSku(getTextOrNull(node, "sku"));
            doc.setName(getTextOrNull(node, "name"));
            doc.setDescription(getTextOrNull(node, "description"));
            doc.setBrand(getTextOrNull(node, "brand"));
            doc.setCategoryId(getTextOrNull(node, "categoryId"));

            if (node.has("basePrice") && !node.get("basePrice").isNull()) {
                doc.setBasePrice(new BigDecimal(node.get("basePrice").asText()));
            }

            doc.setImageUrl(getTextOrNull(node, "imageUrl"));
            doc.setActive(node.has("active") && node.get("active").asBoolean(true));
            doc.setUpdatedAt(Instant.now());

            searchService.indexProduct(doc)
                    .doOnSuccess(d -> log.info(
                            "Indexed product from event. type={}, productId={}",
                            eventType, productId))
                    .doOnError(e -> log.error(
                            "Failed to index product. type={}, productId={}",
                            eventType, productId, e))
                    .subscribe();

        } catch (Exception e) {
            log.error("Failed to process product event. topic={}", topic, e);
        }
    }

    @KafkaListener(
            topics = "catalog.product.deleted",
            groupId = "search-service-indexer"
    )
    public void onProductDeleted(@Payload String payload,
                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        try {
            JsonNode node = objectMapper.readTree(payload);
            String productId = node.get("productId").asText();

            searchService.deleteProduct(productId)
                    .doOnSuccess(v -> log.info(
                            "Removed product from index. productId={}", productId))
                    .subscribe();

        } catch (Exception e) {
            log.error("Failed to process product delete event. topic={}", topic, e);
        }
    }

    private String getTextOrNull(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull()
                ? node.get(field).asText()
                : null;
    }
}

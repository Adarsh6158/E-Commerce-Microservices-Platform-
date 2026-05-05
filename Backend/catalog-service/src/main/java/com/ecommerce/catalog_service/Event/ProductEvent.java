package com.ecommerce.catalog_service.Event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

public record ProductEvent(
        String eventType,
        String productId,
        String sku,
        String name,
        String description,
        String brand,
        String categoryId,
        BigDecimal basePrice,
        String imageUrl,
        boolean active,
        Map<String, Object> attributes,
        Instant timestamp,
        String correlationId
) {

    public static ProductEvent created(
            String id,
            String sku,
            String name,
            String description,
            String brand,
            String categoryId,
            BigDecimal basePrice,
            String imageUrl,
            Map<String, Object> attributes,
            String correlationId
    ) {
        return new ProductEvent(
                "CREATED",
                id,
                sku,
                name,
                description,
                brand,
                categoryId,
                basePrice,
                imageUrl,
                true,
                attributes,
                Instant.now(),
                correlationId
        );
    }

    public static ProductEvent updated(
            String id,
            String sku,
            String name,
            String description,
            String brand,
            String categoryId,
            BigDecimal basePrice,
            String imageUrl,
            boolean active,
            Map<String, Object> attributes,
            String correlationId
    ) {
        return new ProductEvent(
                "UPDATED",
                id,
                sku,
                name,
                description,
                brand,
                categoryId,
                basePrice,
                imageUrl,
                active,
                attributes,
                Instant.now(),
                correlationId
        );
    }

    public static ProductEvent deleted(String id, String correlationId) {
        return new ProductEvent(
                "DELETED",
                id,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                null,
                Instant.now(),
                correlationId
        );
    }
}

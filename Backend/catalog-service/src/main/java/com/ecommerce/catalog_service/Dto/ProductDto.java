package com.ecommerce.catalog_service.Dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public record ProductDto(
        String id,
        String sku,
        String name,
        String description,
        String categoryId,
        String brand,
        BigDecimal basePrice,
        String imageUrl,
        List<String> imageUrls,
        boolean active,
        Double weight,
        Map<String, Object> attributes,
        Instant createdAt,
        Instant updatedAt
) {}

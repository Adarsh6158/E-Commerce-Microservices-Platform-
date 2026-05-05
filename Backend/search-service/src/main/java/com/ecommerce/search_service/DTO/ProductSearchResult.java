package com.ecommerce.search_service.DTO;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductSearchResult(
        String id,
        String name,
        String description,
        String sku,
        String brand,
        String categoryId,
        String categoryName,
        BigDecimal basePrice,
        String imageUrl,
        boolean active,
        Object attributes,
        Instant updatedAt
) {}

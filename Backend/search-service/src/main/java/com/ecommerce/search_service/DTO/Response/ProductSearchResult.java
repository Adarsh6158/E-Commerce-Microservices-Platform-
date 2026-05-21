package com.ecommerce.search_service.Dto.Response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ProductSearchResult(
        String id,
        String name,
        String description,
        String sku,
        String brand,
        String categoryId,
        String categoryName,
        BigDecimal basePrice,
        String image,
        String thumbnail,
        List<String> galleryImages,
        String altText,
        boolean active,
        Object attributes,
        Instant updatedAt
) {}

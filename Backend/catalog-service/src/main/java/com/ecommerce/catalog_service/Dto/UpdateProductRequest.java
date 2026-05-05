package com.ecommerce.catalog_service.Dto;

import java.math.BigDecimal;
import java.util.Map;

public record UpdateProductRequest(
        String name,
        String description,
        String categoryId,
        String brand,
        BigDecimal basePrice,
        String imageUrl,
        Boolean active,
        Double weight,
        Map<String, Object> attributes
) {}

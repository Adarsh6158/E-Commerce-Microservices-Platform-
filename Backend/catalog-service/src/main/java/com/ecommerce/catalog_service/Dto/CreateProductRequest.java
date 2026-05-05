package com.ecommerce.catalog_service.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Map;

public record CreateProductRequest(
        @NotBlank String sku,
        @NotBlank String name,
        String description,
        String categoryId,
        String brand,
        @NotNull BigDecimal basePrice,
        String imageUrl,
        Double weight,
        Map<String, Object> attributes
) {}
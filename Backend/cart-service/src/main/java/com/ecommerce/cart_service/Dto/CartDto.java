package com.ecommerce.cart_service.Dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record CartDto(
        String userId,
        List<CartItemDto> items,
        BigDecimal total,
        int itemCount,
        Instant updatedAt
) {}

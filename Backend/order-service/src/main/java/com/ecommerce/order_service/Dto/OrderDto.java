package com.ecommerce.order_service.Dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderDto(
        String id,
        String userId,
        String status,
        BigDecimal totalAmount,
        String currency,
        String shippingAddress,
        String correlationId,
        String failureReason,
        List<OrderItemDto> items,
        Instant createdAt,
        Instant updatedAt
) {}

package com.ecommerce.inventory_service.Dto;

import java.time.Instant;
import java.util.UUID;

public record ReservationDto(
        UUID id,
        String orderId,
        String productId,
        String sku,
        int quantity,
        String status,
        Instant createdAt,
        Instant updatedAt
) {}

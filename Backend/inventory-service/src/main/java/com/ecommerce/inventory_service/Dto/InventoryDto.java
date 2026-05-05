package com.ecommerce.inventory_service.Dto;

import java.time.Instant;
import java.util.UUID;

public record InventoryDto(
        UUID id,
        String sku,
        String productId,
        String warehouseId,
        int availableQuantity,
        int reservedQuantity,
        int totalStock,
        Instant updatedAt
) {}

package com.ecommerce.order_service.Dto;

import java.math.BigDecimal;

public record OrderItemDto(
        String productId,
        String sku,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {}

package com.ecommerce.cart_service.Dto;

import java.math.BigDecimal;

public record CartItemDto(
        String productId,
        String sku,
        String name,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {}

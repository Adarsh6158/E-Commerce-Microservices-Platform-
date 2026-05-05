package com.ecommerce.order_service.Dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(
        @NotEmpty List<@Valid Item> items
) {
    public record Item(
            @NotBlank String productId,
            @NotBlank String sku,
            @NotBlank String productName,
            @NotNull @Min(1) Integer quantity,
            @NotNull BigDecimal unitPrice
    ) {}
}

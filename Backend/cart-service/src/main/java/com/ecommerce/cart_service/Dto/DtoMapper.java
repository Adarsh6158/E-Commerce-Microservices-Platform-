package com.ecommerce.cart_service.Dto;

import com.ecommerce.cart_service.Domain.Cart;
import com.ecommerce.cart_service.Domain.CartItem;

public final class DtoMapper {

    private DtoMapper() {}

    public static CartDto toDto(Cart cart) {
        var items = cart.getItems().stream().map(DtoMapper::toDto).toList();
        return new CartDto(cart.getUserId(), items, cart.getTotal(), cart.getItemCount(), cart.getUpdatedAt());
    }

    public static CartItemDto toDto(CartItem item) {
        return new CartItemDto(
                item.getProductId(),
                item.getSku(),
                item.getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal()
        );
    }

    public static CartItem toEntity(AddCartItemRequest r) {
        return new CartItem(r.productId(), r.sku(), r.name(), r.quantity(), r.unitPrice());
    }
}

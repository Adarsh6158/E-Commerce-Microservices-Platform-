package com.ecommerce.cart_service.Domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Cart {

    private String userId;
    private List<CartItem> items = new ArrayList<>();
    private Instant updatedAt;

    public Cart() {}

    public Cart(String userId) {
        this.userId = userId;
        this.updatedAt = Instant.now();
    }

    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }

    public List<CartItem> getItems() { return items; }

    public void setItems(List<CartItem> items) { this.items = items; }

    public Instant getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public BigDecimal getTotal() {
        return items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getItemCount() {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}

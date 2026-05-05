package com.ecommerce.inventory_service.Domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.time.Instant;
import java.util.UUID;

@Table("reservations")
public class Reservation {

    @Id
    private UUID id;

    @Column("order_id")
    private String orderId;

    @Column("product_id")
    private String productId;

    @Column("sku")
    private String sku;

    @Column("quantity")
    private int quantity;

    @Column("status")
    private String status; // RESERVED, CONFIRMED, RELEASED

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;

    public Reservation() {}

    public UUID getId() { return id; }

    public void setId(UUID id) { this.id = id; }

    public String getOrderId() { return orderId; }

    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getProductId() { return productId; }

    public void setProductId(String productId) { this.productId = productId; }

    public String getSku() { return sku; }

    public void setSku(String sku) { this.sku = sku; }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }

    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}

package com.ecommerce.inventory_service.Domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.time.Instant;
import java.util.UUID;

@Table("inventory")
public class Inventory {

    @Id
    private UUID id;

    @Column("sku")
    private String sku;

    @Column("product_id")
    private String productId;

    @Column("warehouse_id")
    private String warehouseId;

    @Column("available_quantity")
    private int availableQuantity;

    @Column("reserved_quantity")
    private int reservedQuantity;

    @Version
    @Column("version")
    private Long version; // Optimistic locking

    @Column("updated_at")
    private Instant updatedAt;

    public Inventory() {}

    public UUID getId() { return id; }

    public void setId(UUID id) { this.id = id; }

    public String getSku() { return sku; }

    public void setSku(String sku) { this.sku = sku; }

    public String getProductId() { return productId; }

    public void setProductId(String productId) { this.productId = productId; }

    public String getWarehouseId() { return warehouseId; }

    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }

    public int getAvailableQuantity() { return availableQuantity; }

    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }

    public int getReservedQuantity() { return reservedQuantity; }

    public void setReservedQuantity(int reservedQuantity) { this.reservedQuantity = reservedQuantity; }

    public Long getVersion() { return version; }

    public void setVersion(Long version) { this.version = version; }

    public Instant getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public int getTotalStock() { return availableQuantity + reservedQuantity; }
}

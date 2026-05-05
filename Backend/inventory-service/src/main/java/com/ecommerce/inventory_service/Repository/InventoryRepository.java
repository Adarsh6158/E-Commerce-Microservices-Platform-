package com.ecommerce.inventory_service.Repository;

import com.ecommerce.inventory_service.Domain.Inventory;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface InventoryRepository extends R2dbcRepository<Inventory, UUID> {
    Mono<Inventory> findByProductId(String productId);
    Mono<Inventory> findBySku(String sku);
}

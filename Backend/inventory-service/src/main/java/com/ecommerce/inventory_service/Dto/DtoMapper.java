package com.ecommerce.inventory_service.Dto;

import com.ecommerce.inventory_service.Domain.Inventory;
import com.ecommerce.inventory_service.Domain.Reservation;

public final class DtoMapper {

    private DtoMapper() {}

    public static InventoryDto toDto(Inventory i) {
        return new InventoryDto(
                i.getId(),
                i.getSku(),
                i.getProductId(),
                i.getWarehouseId(),
                i.getAvailableQuantity(),
                i.getReservedQuantity(),
                i.getTotalStock(),
                i.getUpdatedAt()
        );
    }

    public static ReservationDto toDto(Reservation r) {
        return new ReservationDto(
                r.getId(),
                r.getOrderId(),
                r.getProductId(),
                r.getSku(),
                r.getQuantity(),
                r.getStatus(),
                r.getCreatedAt(),
                r.getUpdatedAt()
        );
    }
}

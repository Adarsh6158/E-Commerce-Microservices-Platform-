package com.ecommerce.order_service.Dto;

import com.ecommerce.order_service.Domain.Order;
import com.ecommerce.order_service.Domain.OrderItem;

import java.util.List;

public final class DtoMapper {

    private DtoMapper() {}

    public static OrderDto toDto(Order order) {
        List<OrderItemDto> items = order.getItems() != null
                ? order.getItems().stream().map(DtoMapper::toDto).toList()
                : List.of();

        return new OrderDto(
                order.getId(),
                order.getUserId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCurrency(),
                order.getShippingAddress(),
                order.getCorrelationId(),
                order.getFailureReason(),
                items,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    public static OrderItemDto toDto(OrderItem item) {
        return new OrderItemDto(
                item.getProductId(),
                item.getSku(),
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal()
        );
    }
}

package com.ecommerce.inventory_service.Dto;

public record AddStockRequest(String productId, String sku, int quantity, String warehouseId) {}

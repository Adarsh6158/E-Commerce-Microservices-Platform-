package com.ecommerce.inventory_service.Controller;

import com.ecommerce.inventory_service.Dto.AddStockRequest;
import com.ecommerce.inventory_service.Dto.DtoMapper;
import com.ecommerce.inventory_service.Dto.InventoryDto;
import com.ecommerce.inventory_service.Dto.ReservationDto;
import com.ecommerce.inventory_service.Service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{productId}")
    public Mono<InventoryDto> getStock(@PathVariable String productId) {
        return inventoryService.getStock(productId).map(DtoMapper::toDto);
    }

    @GetMapping("/{productId}/availability")
    public Mono<Map<String, Object>> checkAvailability(@PathVariable String productId,
                                                       @RequestParam int quantity) {
        return inventoryService.checkAvailability(productId, quantity)
                .map(available -> Map.<String, Object>of(
                        "productId", productId,
                        "available", available,
                        "requestedQuantity", quantity
                ));
    }

    @PostMapping("/stock")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<InventoryDto> addStock(@RequestBody AddStockRequest request) {
        return inventoryService.addStock(
                request.productId(),
                request.sku(),
                request.quantity(),
                request.warehouseId()
        ).map(DtoMapper::toDto);
    }

    @GetMapping("/reservations/{orderId}")
    public Flux<ReservationDto> getReservations(@PathVariable String orderId) {
        return inventoryService.getReservations(orderId).map(DtoMapper::toDto);
    }
}
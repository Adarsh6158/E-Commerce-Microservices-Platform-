package com.ecommerce.inventory_service.Service;

import com.ecommerce.inventory_service.Domain.Inventory;
import com.ecommerce.inventory_service.Domain.Reservation;
import com.ecommerce.inventory_service.Repository.InventoryRepository;
import com.ecommerce.inventory_service.Repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryRepository inventoryRepository;
    private final ReservationRepository reservationRepository;
    private final DistributedLockService lockService;

    public InventoryService(InventoryRepository inventoryRepository,
                            ReservationRepository reservationRepository,
                            DistributedLockService lockService) {
        this.inventoryRepository = inventoryRepository;
        this.reservationRepository = reservationRepository;
        this.lockService = lockService;
    }

    public Mono<Inventory> getStock(String productId) {
        return inventoryRepository.findByProductId(productId);
    }

    public Mono<Boolean> checkAvailability(String productId, int quantity) {
        return inventoryRepository.findByProductId(productId)
                .map(inv -> inv.getAvailableQuantity() >= quantity)
                .defaultIfEmpty(false);
    }

    /**
     * Reserve stock for an order.
     * - distributed lock (prevents overselling)
     * - same order+product won't double reserve
     */
    public Mono<Reservation> reserveStock(String orderId, String productId, String sku, int quantity) {

        return reservationRepository.existsByOrderIdAndProductId(orderId, productId)
                .flatMap(exists -> {
                    if (exists) {
                        log.info("Reservation already exists. orderId={}, productId={}", orderId, productId);
                        return reservationRepository.findByOrderIdAndProductId(orderId, productId);
                    }
                    return doReserve(orderId, productId, sku, quantity);
                });
    }

    private Mono<Reservation> doReserve(String orderId, String productId, String sku, int quantity) {

        String lockResource = productId;

        return lockService.acquireLock(lockResource)
                .switchIfEmpty(Mono.error(new IllegalStateException("Could not acquire lock for product: " + productId)))
                .flatMap(lockValue ->
                        inventoryRepository.findByProductId(productId)
                                .switchIfEmpty(Mono.error(new IllegalArgumentException("Product not found: " + productId)))
                                .flatMap(inventory -> {

                                    if (inventory.getAvailableQuantity() < quantity) {
                                        return Mono.error(new IllegalStateException(
                                                "Insufficient stock. available=" + inventory.getAvailableQuantity() +
                                                        ", requested=" + quantity));
                                    }

                                    // Update inventory
                                    inventory.setAvailableQuantity(inventory.getAvailableQuantity() - quantity);
                                    inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
                                    inventory.setUpdatedAt(Instant.now());

                                    // Create reservation
                                    Reservation reservation = new Reservation();
                                    reservation.setOrderId(orderId);
                                    reservation.setProductId(productId);
                                    reservation.setSku(sku);
                                    reservation.setQuantity(quantity);
                                    reservation.setStatus("RESERVED");
                                    reservation.setCreatedAt(Instant.now());
                                    reservation.setUpdatedAt(Instant.now());

                                    return inventoryRepository.save(inventory)
                                            .then(reservationRepository.save(reservation));
                                })
                                .doOnSuccess(r ->
                                        log.info("Stock reserved. orderId={}, productId={}, qty={}",
                                                orderId, productId, quantity))
                                .doOnError(e ->
                                        log.error("Stock reservation failed. orderId={}, productId={}",
                                                orderId, productId, e))
                                .doFinally(signal ->
                                        lockService.releaseLock(lockResource, lockValue).subscribe())
                );
    }

    
     // Release reserved stock
     
    public Mono<Void> releaseStock(String orderId) {
        return reservationRepository.findByOrderId(orderId)
                .filter(r -> "RESERVED".equals(r.getStatus()))
                .flatMap(reservation -> {

                    reservation.setStatus("RELEASED");
                    reservation.setUpdatedAt(Instant.now());

                    return inventoryRepository.findByProductId(reservation.getProductId())
                            .flatMap(inventory -> {
                                inventory.setAvailableQuantity(
                                        inventory.getAvailableQuantity() + reservation.getQuantity());
                                inventory.setReservedQuantity(
                                        Math.max(0,
                                                inventory.getReservedQuantity() - reservation.getQuantity()));
                                inventory.setUpdatedAt(Instant.now());
                                return inventoryRepository.save(inventory);
                            })
                            .then(reservationRepository.save(reservation));
                })
                .then()
                .doOnSuccess(v -> log.info("Stock released for orderId={}", orderId));
    }

    
     // Confirm reservation (RESERVED -> CONFIRMED)
    public Mono<Void> confirmReservation(String orderId) {
        return reservationRepository.findByOrderId(orderId)
                .filter(r -> "RESERVED".equals(r.getStatus()))
                .flatMap(reservation -> {

                    reservation.setStatus("CONFIRMED");
                    reservation.setUpdatedAt(Instant.now());

                    return inventoryRepository.findByProductId(reservation.getProductId())
                            .flatMap(inventory -> {
                                inventory.setReservedQuantity(
                                        Math.max(0,
                                                inventory.getReservedQuantity() - reservation.getQuantity()));
                                inventory.setUpdatedAt(Instant.now());
                                return inventoryRepository.save(inventory);
                            })
                            .then(reservationRepository.save(reservation));
                })
                .then()
                .doOnSuccess(v -> log.info("Reservation confirmed for orderId={}", orderId));
    }

    public Mono<Inventory> addStock(String productId, String sku, int quantity, String warehouseId) {
        return inventoryRepository.findByProductId(productId)
                .flatMap(inv -> {
                    inv.setAvailableQuantity(inv.getAvailableQuantity() + quantity);
                    inv.setUpdatedAt(Instant.now());
                    return inventoryRepository.save(inv);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    Inventory inv = new Inventory();
                    inv.setProductId(productId);
                    inv.setSku(sku);
                    inv.setWarehouseId(warehouseId);
                    inv.setAvailableQuantity(quantity);
                    inv.setReservedQuantity(0);
                    inv.setUpdatedAt(Instant.now());
                    return inventoryRepository.save(inv);
                }));
    }

    public Flux<Reservation> getReservations(String orderId) {
        return reservationRepository.findByOrderId(orderId);
    }
}

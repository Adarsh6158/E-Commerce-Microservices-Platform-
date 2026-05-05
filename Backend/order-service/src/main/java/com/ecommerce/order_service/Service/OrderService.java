package com.ecommerce.order_service.Service;

import com.ecommerce.order_service.Domain.Order;
import com.ecommerce.order_service.Domain.OrderItem;
import com.ecommerce.order_service.Repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_INVENTORY_RESERVED = "INVENTORY_RESERVED";
    public static final String STATUS_PAYMENT_PROCESSING = "PAYMENT_PROCESSING";
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_FAILED = "FAILED";

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderService(OrderRepository orderRepository,
                        KafkaTemplate<String, Object> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Mono<Order> createOrder(String userId, List<OrderItemRequest> items) {
        String correlationId = UUID.randomUUID().toString();

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(STATUS_PENDING);
        order.setCorrelationId(correlationId);
        order.setCreatedAt(Instant.now());
        order.setUpdatedAt(Instant.now());

        BigDecimal totalAmount = items.stream()
                .map(i -> i.unitPrice().multiply(BigDecimal.valueOf(i.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(totalAmount);

        List<OrderItem> orderItems = items.stream().map(i -> {
            OrderItem item = new OrderItem();
            item.setProductId(i.productId());
            item.setSku(i.sku());
            item.setProductName(i.productName());
            item.setQuantity(i.quantity());
            item.setUnitPrice(i.unitPrice());
            item.setSubtotal(i.unitPrice().multiply(BigDecimal.valueOf(i.quantity())));
            return item;
        }).toList();

        order.setItems(orderItems);

        return orderRepository.save(order)
                .doOnSuccess(savedOrder -> {
                    log.info("Order created: orderId={}, correlationId={}", savedOrder.getId(), correlationId);
                    publishOrderEvent("order.created", savedOrder);
                });
    }

    public Mono<Order> getOrder(String orderId) {
        return orderRepository.findById(orderId);
    }

    public Flux<Order> getOrdersByUserId(String userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Mono<Order> updateOrderStatus(String orderId, String newStatus, String failureReason) {
        return orderRepository.findById(orderId)
                .flatMap(order -> {
                    order.setStatus(newStatus);
                    order.setUpdatedAt(Instant.now());

                    if (failureReason != null) {
                        order.setFailureReason(failureReason);
                    }

                    return orderRepository.save(order);
                })
                .doOnSuccess(order ->
                        log.info("Order status updated: orderId={}, status={}", orderId, newStatus));
    }

    public Mono<Order> handleInventoryReserved(String orderId) {
        return updateOrderStatus(orderId, STATUS_INVENTORY_RESERVED, null)
                .doOnSuccess(order -> {
                    log.info("Inventory reserved for order: {}", orderId);
                    updateOrderStatus(orderId, STATUS_PAYMENT_PROCESSING, null)
                            .doOnSuccess(o -> publishOrderEvent("order.payment-requested", o))
                            .subscribe();
                });
    }

    public Mono<Order> handleInventoryReserveFailed(String orderId, String reason) {
        return updateOrderStatus(orderId, STATUS_FAILED, "Inventory reservation failed: " + reason)
                .doOnSuccess(order -> {
                    log.warn("Inventory reservation failed for order: {}, reason: {}", orderId, reason);
                    publishOrderEvent("order.failed", order);
                });
    }

    public Mono<Order> handlePaymentCompleted(String orderId) {
        return updateOrderStatus(orderId, STATUS_CONFIRMED, null)
                .doOnSuccess(order -> {
                    log.info("Payment completed, order confirmed: {}", orderId);
                    publishOrderEvent("order.confirmed", order);
                });
    }

    public Mono<Order> handlePaymentFailed(String orderId, String reason) {
        return updateOrderStatus(orderId, STATUS_FAILED, "Payment failed: " + reason)
                .doOnSuccess(order -> {
                    log.warn("Payment failed for order: {}, triggering compensation", orderId);
                    publishOrderEvent("order.cancelled", order);
                });
    }

    public Mono<Order> cancelOrder(String orderId, String userId) {
        return orderRepository.findById(orderId)
                .filter(order -> order.getUserId().equals(userId))
                .filter(order -> STATUS_PENDING.equals(order.getStatus())
                        || STATUS_INVENTORY_RESERVED.equals(order.getStatus()))
                .flatMap(order -> {
                    order.setStatus(STATUS_CANCELLED);
                    order.setUpdatedAt(Instant.now());
                    order.setFailureReason("Cancelled by user");
                    return orderRepository.save(order);
                })
                .doOnSuccess(order -> {
                    if (order != null) {
                        log.info("Order cancelled by user: {}", orderId);
                        publishOrderEvent("order.cancelled", order);
                    }
                });
    }

    public Mono<Boolean> hasUserPurchasedProduct(String userId, String productId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .filter(order -> STATUS_CONFIRMED.equals(order.getStatus()))
                .any(order -> order.getItems() != null &&
                        order.getItems().stream()
                                .anyMatch(item -> productId.equals(item.getProductId())));
    }

    private void publishOrderEvent(String topic, Order order) {
        try {
            List<OrderEventItem> eventItems = order.getItems() != null
                    ? order.getItems().stream()
                    .map(i -> new OrderEventItem(
                            i.getProductId(),
                            i.getSku(),
                            i.getProductName(),
                            i.getQuantity(),
                            i.getUnitPrice()))
                    .toList()
                    : List.of();

            var event = new OrderEvent(
                    order.getId(),
                    order.getUserId(),
                    order.getStatus(),
                    order.getTotalAmount(),
                    order.getCorrelationId(),
                    order.getFailureReason(),
                    eventItems
            );

            kafkaTemplate.send(topic, order.getId(), event);
            log.debug("Published event to {}: orderId={}", topic, order.getId());

        } catch (Exception e) {
            log.error("Failed to publish event to {}: {}", topic, e.getMessage(), e);
        }
    }

    public record OrderItemRequest(
            String productId,
            String sku,
            String productName,
            int quantity,
            BigDecimal unitPrice
    ) {}

    public record OrderEventItem(
            String productId,
            String sku,
            String productName,
            int quantity,
            BigDecimal unitPrice
    ) {}

    public record OrderEvent(
            String orderId,
            String userId,
            String status,
            BigDecimal totalAmount,
            String correlationId,
            String failureReason,
            List<OrderEventItem> items
    ) {}
}

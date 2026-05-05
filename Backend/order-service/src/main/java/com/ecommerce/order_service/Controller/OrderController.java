package com.ecommerce.order_service.Controller;

import com.ecommerce.order_service.Dto.CreateOrderRequest;
import com.ecommerce.order_service.Dto.DtoMapper;
import com.ecommerce.order_service.Dto.OrderDto;
import com.ecommerce.order_service.Service.InvoiceService;
import com.ecommerce.order_service.Service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final InvoiceService invoiceService;

    public OrderController(OrderService orderService, InvoiceService invoiceService) {
        this.orderService = orderService;
        this.invoiceService = invoiceService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<OrderDto> createOrder(@RequestHeader("X-User-Id") String userId,
                                      @Valid @RequestBody CreateOrderRequest request) {

        List<OrderService.OrderItemRequest> items = request.items().stream()
                .map(i -> new OrderService.OrderItemRequest(
                        i.productId(), i.sku(), i.productName(), i.quantity(), i.unitPrice()))
                .toList();

        return orderService.createOrder(userId, items).map(DtoMapper::toDto);
    }

    @GetMapping("/{orderId}")
    public Mono<OrderDto> getOrder(@PathVariable String orderId) {
        return orderService.getOrder(orderId).map(DtoMapper::toDto);
    }

    @GetMapping
    public Flux<OrderDto> getUserOrders(@RequestHeader("X-User-Id") String userId) {
        return orderService.getOrdersByUserId(userId).map(DtoMapper::toDto);
    }

    @PutMapping("/{orderId}/cancel")
    public Mono<OrderDto> cancelOrder(@PathVariable String orderId,
                                      @RequestHeader("X-User-Id") String userId) {
        return orderService.cancelOrder(orderId, userId).map(DtoMapper::toDto);
    }

    @GetMapping("/{orderId}/invoice")
    public Mono<ResponseEntity<byte[]>> getInvoice(@PathVariable String orderId) {
        return invoiceService.generateInvoice(orderId)
                .map(pdf -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=invoice-" + orderId.substring(0, Math.min(8, orderId.length())) + ".pdf")
                        .contentType(MediaType.APPLICATION_PDF)
                        .contentLength(pdf.length)
                        .body(pdf));
    }

    @GetMapping("/verify-purchase")
    public Mono<Map<String, Object>> verifyPurchase(
            @RequestParam String userId,
            @RequestParam String productId) {

        return orderService.hasUserPurchasedProduct(userId, productId)
                .map(purchased -> Map.<String, Object>of(
                        "userId", userId,
                        "productId", productId,
                        "purchased", purchased
                ));
    }
}

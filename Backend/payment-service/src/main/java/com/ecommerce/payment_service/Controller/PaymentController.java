package com.ecommerce.payment_service.Controller;

import com.ecommerce.payment_service.Dto.DtoMapper;
import com.ecommerce.payment_service.Dto.PaymentDto;
import com.ecommerce.payment_service.Service.PaymentService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/order/{orderId}")
    public Mono<PaymentDto> getPaymentByOrderId(@PathVariable String orderId) {
        return paymentService.getPaymentByOrderId(orderId).map(DtoMapper::toDto);
    }

    @GetMapping
    public Flux<PaymentDto> getUserPayments(@RequestHeader("X-User-Id") String userId) {
        return paymentService.getPaymentsByUserId(userId).map(DtoMapper::toDto);
    }

    @PostMapping("/order/{orderId}/refund")
    public Mono<PaymentDto> refundPayment(@PathVariable String orderId) {
        return paymentService.refundPayment(orderId).map(DtoMapper::toDto);
    }
}

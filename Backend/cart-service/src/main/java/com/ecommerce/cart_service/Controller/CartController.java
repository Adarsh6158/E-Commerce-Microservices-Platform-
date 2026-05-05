package com.ecommerce.cart_service.Controller;

import com.ecommerce.cart_service.Dto.AddCartItemRequest;
import com.ecommerce.cart_service.Dto.CartDto;
import com.ecommerce.cart_service.Dto.DtoMapper;
import com.ecommerce.cart_service.Service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public Mono<CartDto> getCart(
            @RequestHeader(value = "X-User-Id", defaultValue = "guest:anonymous") String userId) {
        return cartService.getCart(userId).map(DtoMapper::toDto);
    }

    @PostMapping("/items")
    public Mono<CartDto> addItem(
            @RequestHeader(value = "X-User-Id", defaultValue = "guest:anonymous") String userId,
            @Valid @RequestBody AddCartItemRequest request) {
        return cartService.addItem(userId, DtoMapper.toEntity(request))
                .map(DtoMapper::toDto);
    }

    @PutMapping("/items/{productId}")
    public Mono<CartDto> updateQuantity(
            @RequestHeader(value = "X-User-Id", defaultValue = "guest:anonymous") String userId,
            @PathVariable String productId,
            @RequestParam int quantity) {
        return cartService.updateItemQuantity(userId, productId, quantity)
                .map(DtoMapper::toDto);
    }

    @DeleteMapping("/items/{productId}")
    public Mono<CartDto> removeItem(
            @RequestHeader(value = "X-User-Id", defaultValue = "guest:anonymous") String userId,
            @PathVariable String productId) {
        return cartService.removeItem(userId, productId)
                .map(DtoMapper::toDto);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> clearCart(
            @RequestHeader(value = "X-User-Id", defaultValue = "guest:anonymous") String userId) {
        return cartService.clearCart(userId);
    }

    @PostMapping("/merge")
    public Mono<CartDto> mergeCarts(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam String guestId) {
        return cartService.mergeCarts(guestId, userId)
                .map(DtoMapper::toDto);
    }
}

package com.ecommerce.cart_service.Service;

import com.ecommerce.cart_service.Domain.Cart;
import com.ecommerce.cart_service.Domain.CartItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Service
public class CartService {

    private static final Logger log = LoggerFactory.getLogger(CartService.class);
    private static final String CART_PREFIX = "cart:";
    private static final Duration AUTHENTICATED_TTL = Duration.ofDays(30);
    private static final Duration GUEST_TTL = Duration.ofDays(7);

    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public CartService(ReactiveStringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public Mono<Cart> getCart(String userId) {
        return redisTemplate.opsForValue().get(CART_PREFIX + userId)
                .flatMap(json -> deserialize(json, userId))
                .defaultIfEmpty(new Cart(userId))
                .doOnNext(c -> log.debug("Cart fetched. userId={}, items={}", userId, c.getItemCount()));
    }

    public Mono<Cart> addItem(String userId, CartItem item) {
        return getCart(userId)
                .flatMap(cart -> {
                    // Merge with existing item or add new
                    cart.getItems().stream()
                            .filter(i -> i.getProductId().equals(item.getProductId()))
                            .findFirst()
                            .ifPresentOrElse(
                                    existing -> existing.setQuantity(existing.getQuantity() + item.getQuantity()),
                                    () -> cart.getItems().add(item)
                            );

                    cart.setUpdatedAt(Instant.now());
                    return saveCart(userId, cart);
                })
                .doOnSuccess(c -> log.info("Item added to cart. userId={}, productId={}, qty={}",
                        userId, item.getProductId(), item.getQuantity()));
    }

    public Mono<Cart> updateItemQuantity(String userId, String productId, int quantity) {
        return getCart(userId)
                .flatMap(cart -> {
                    if (quantity <= 0) {
                        cart.getItems().removeIf(i -> i.getProductId().equals(productId));
                    } else {
                        cart.getItems().stream()
                                .filter(i -> i.getProductId().equals(productId))
                                .findFirst()
                                .ifPresent(i -> i.setQuantity(quantity));
                    }

                    cart.setUpdatedAt(Instant.now());
                    return saveCart(userId, cart);
                });
    }

    public Mono<Cart> removeItem(String userId, String productId) {
        return updateItemQuantity(userId, productId, 0);
    }

    public Mono<Void> clearCart(String userId) {
        return redisTemplate.delete(CART_PREFIX + userId)
                .then()
                .doOnSuccess(v -> log.info("Cart cleared. userId={}", userId));
    }

    public Mono<Cart> mergeCarts(String guestId, String userId) {
        return Mono.zip(getCart(guestId), getCart(userId))
                .flatMap(tuple -> {
                    Cart guestCart = tuple.getT1();
                    Cart userCart = tuple.getT2();

                    // Merge guest items into user cart (user cart items take priority)
                    for (CartItem guestItem : guestCart.getItems()) {
                        boolean exists = userCart.getItems().stream()
                                .anyMatch(i -> i.getProductId().equals(guestItem.getProductId()));

                        if (!exists) {
                            userCart.getItems().add(guestItem);
                        }
                    }

                    userCart.setUpdatedAt(Instant.now());
                    return saveCart(userId, userCart)
                            .flatMap(c -> clearCart(guestId).thenReturn(c));
                })
                .doOnSuccess(c -> log.info("Carts merged. guestId={} -> userId={}", guestId, userId));
    }

    private Mono<Cart> saveCart(String userId, Cart cart) {
        try {
            String json = objectMapper.writeValueAsString(cart);
            Duration ttl = userId.startsWith("guest:") ? GUEST_TTL : AUTHENTICATED_TTL;

            return redisTemplate.opsForValue()
                    .set(CART_PREFIX + userId, json, ttl)
                    .thenReturn(cart);
        } catch (JsonProcessingException e) {
            return Mono.error(new RuntimeException("Failed to serialize cart", e));
        }
    }

    private Mono<Cart> deserialize(String json, String userId) {
        try {
            return Mono.just(objectMapper.readValue(json, Cart.class));
        } catch (JsonProcessingException e) {
            log.warn("Failed to deserialize cart. userId={}, clearing corrupted data", userId);
            return redisTemplate.delete(CART_PREFIX + userId).then(Mono.empty());
        }
    }
}

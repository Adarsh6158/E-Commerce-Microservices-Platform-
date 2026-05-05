package com.ecommerce.api_gateway.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    private static final Logger log = LoggerFactory.getLogger(FallbackController.class);

    @GetMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> authFallback(ServerWebExchange exchange) {
        return buildFallbackResponse(exchange, "Auth Service");
    }

    @GetMapping(value = "/catalog", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> catalogFallback(ServerWebExchange exchange) {
        return buildFallbackResponse(exchange, "Catalog Service");
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> searchFallback(ServerWebExchange exchange) {
        return buildFallbackResponse(exchange, "Search Service");
    }

    @GetMapping(value = "/cart", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> cartFallback(ServerWebExchange exchange) {
        return buildFallbackResponse(exchange, "Cart Service");
    }

    @GetMapping(value = "/pricing", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> pricingFallback(ServerWebExchange exchange) {
        return buildFallbackResponse(exchange, "Pricing Service");
    }

    @GetMapping(value = "/inventory", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> inventoryFallback(ServerWebExchange exchange) {
        return buildFallbackResponse(exchange, "Inventory Service");
    }

    @GetMapping(value = "/order", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> orderFallback(ServerWebExchange exchange) {
        return buildFallbackResponse(exchange, "Order Service");
    }

    @GetMapping(value = "/payment", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> paymentFallback(ServerWebExchange exchange) {
        return buildFallbackResponse(exchange, "Payment Service");
    }

    @GetMapping(value = "/notification", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> notificationFallback(ServerWebExchange exchange) {
        return buildFallbackResponse(exchange, "Notification Service");
    }

    @GetMapping(value = "/analytics", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> analyticsFallback(ServerWebExchange exchange) {
        return buildFallbackResponse(exchange, "Analytics Service");
    }

    private Mono<Map<String, Object>> buildFallbackResponse(ServerWebExchange exchange, String serviceName) {
        String correlationId = exchange.getAttribute("correlationId");

        log.warn("Circuit breaker fallback triggered. service={}, correlationId={}", serviceName, correlationId);

        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", 503);
        body.put("error", "Service Unavailable");
        body.put("message", serviceName + " is temporarily unavailable. Please retry later.");
        body.put("correlationId", correlationId);

        return Mono.just(body);
    }
}
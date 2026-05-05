package com.ecommerce.api_gateway.Filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        long startTime = System.currentTimeMillis();
        ServerHttpRequest request = exchange.getRequest();

        String correlationId = exchange.getAttribute("correlationId");
        String userId = exchange.getAttribute("userId");
        String method = request.getMethod().name();
        String path = request.getURI().getPath();
        String query = request.getURI().getQuery();

        String clientIp = request.getRemoteAddress() != null
                ? request.getRemoteAddress().getAddress().getHostAddress()
                : "unknown";

        log.info(">>> GATEWAY REQUEST: method={}, path={}, query={}, clientIp={}, userId={}, correlationId={}",
                method, path, query, clientIp, userId, correlationId);

        return chain.filter(exchange)
                .doFinally(signalType -> {

                    long duration = System.currentTimeMillis() - startTime;
                    ServerHttpResponse response = exchange.getResponse();

                    int statusCode = response.getStatusCode() != null
                            ? response.getStatusCode().value()
                            : 0;

                    if (statusCode >= 500) {
                        log.error("<<< GATEWAY RESPONSE: method={}, path={}, status={}, duration={}ms, correlationId={}",
                                method, path, statusCode, duration, correlationId);
                    } else if (statusCode >= 400) {
                        log.warn("<<< GATEWAY RESPONSE: method={}, path={}, status={}, duration={}ms, correlationId={}",
                                method, path, statusCode, duration, correlationId);
                    } else {
                        log.info("<<< GATEWAY RESPONSE: method={}, path={}, status={}, duration={}ms, correlationId={}",
                                method, path, statusCode, duration, correlationId);
                    }
                });
    }

    @Override
    public int getOrder() {
        // After auth filter, before routing
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}
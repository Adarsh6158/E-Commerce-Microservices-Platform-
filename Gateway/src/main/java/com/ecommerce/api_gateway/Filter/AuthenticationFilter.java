package com.ecommerce.api_gateway.Filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final JwtParser jwtParser;
    private final List<String> excludedPaths;

    public AuthenticationFilter(
            JwtParser jwtParser,
            @Value("${gateway.auth.excluded-paths}") List<String> excludedPaths) {
        this.jwtParser = jwtParser;
        this.excludedPaths = excludedPaths;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String correlationId = exchange.getAttribute("correlationId");

        // Allow CORS preflight
        if (request.getMethod() == org.springframework.http.HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        if (isExcludedPath(path)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("Missing or invalid Authorization header. path={}, correlationId={}", path, correlationId);
            return onUnauthorized(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        try {
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();

            String userId = claims.getSubject();
            String roles = claims.get("roles", String.class);

            if (userId == null) {
                log.warn("JWT missing subject claim. correlationId={}", correlationId);
                return onUnauthorized(exchange, "Invalid token: missing subject");
            }

            // Store userId for rate limiting
            exchange.getAttributes().put("userId", userId);

            ServerHttpRequest mutatedRequest = request.mutate()
                    .headers(headers -> {
                        headers.remove("X-User-Id");
                        headers.remove("X-User-Roles");

                        headers.set("X-User-Id", userId);
                        if (roles != null) {
                            headers.set("X-User-Roles", roles);
                        }
                    })
                    .build();

            log.debug("JWT validated. userId={}, roles={}, path={}, correlationId={}",
                    userId, roles, path, correlationId);

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (ExpiredJwtException e) {
            log.info("JWT expired. correlationId={}", correlationId);
            return onUnauthorized(exchange, "Token expired");

        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT. correlationId={}", correlationId);
            return onUnauthorized(exchange, "Invalid token format");

        } catch (SignatureException e) {
            log.warn("JWT signature verification failed. correlationId={}", correlationId);
            return onUnauthorized(exchange, "Invalid token signature");

        } catch (Exception e) {
            log.error("JWT validation failed unexpectedly. correlationId={}", correlationId, e);
            return onUnauthorized(exchange, "Authentication failed");
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    private boolean isExcludedPath(String path) {
        return excludedPaths.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private Mono<Void> onUnauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = String.format(
                "{\"error\":\"Unauthorized\",\"message\":\"%s\",\"correlationId\":\"%s\"}",
                message,
                exchange.getAttribute("correlationId")
        );

        DataBuffer buffer = response.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }
}

package com.ecommerce.search_service.Exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidSearchRequestException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleInvalidSearchRequest(InvalidSearchRequestException ex) {
        log.warn("Invalid search request: {}", ex.getMessage());
        return Mono.just(buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(IndexingException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleIndexingException(IndexingException ex) {
        log.error("Indexing error: {}", ex.getMessage(), ex);
        return Mono.just(buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Indexing operation failed"));
    }

    @ExceptionHandler(SearchException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleSearchException(SearchException ex) {
        log.error("Search error: {}", ex.getMessage(), ex);
        return Mono.just(buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Search operation failed"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return Mono.just(buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return Mono.just(buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred"));
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> body = Map.of(
                "success", false,
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message,
                "timestamp", Instant.now().toString()
        );
        return ResponseEntity.status(status).body(body);
    }
}

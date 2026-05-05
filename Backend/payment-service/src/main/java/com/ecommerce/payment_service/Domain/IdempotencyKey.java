package com.ecommerce.payment_service.Domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("idempotency_keys")
public class IdempotencyKey {

    @Id
    private UUID id;
    private String idempotencyKey;
    private String orderId;
    private String responseBody;
    private Integer responseStatus;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public UUID getId() { return id; }

    public void setId(UUID id) { this.id = id; }

    public String getIdempotencyKey() { return idempotencyKey; }

    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }

    public String getOrderId() { return orderId; }

    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getResponseBody() { return responseBody; }

    public void setResponseBody(String responseBody) { this.responseBody = responseBody; }

    public Integer getResponseStatus() { return responseStatus; }

    public void setResponseStatus(Integer responseStatus) { this.responseStatus = responseStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }

    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}
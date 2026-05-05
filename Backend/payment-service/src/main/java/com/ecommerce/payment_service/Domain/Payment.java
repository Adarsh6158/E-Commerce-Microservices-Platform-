package com.ecommerce.payment_service.Domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("payments")
public class Payment {

    @Id
    private UUID id;
    private String orderId;
    private String userId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private String transactionRef;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    public enum PaymentStatus {
        PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED
    }

    public enum PaymentMethod {
        CREDIT_CARD, DEBIT_CARD, PAYPAL, BANK_TRANSFER
    }

    public UUID getId() { return id; }

    public void setId(UUID id) { this.id = id; }

    public String getOrderId() { return orderId; }

    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }

    public BigDecimal getAmount() { return amount; }

    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }

    public void setCurrency(String currency) { this.currency = currency; }

    public PaymentStatus getStatus() { return status; }

    public void setStatus(PaymentStatus status) { this.status = status; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }

    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getTransactionRef() { return transactionRef; }

    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }

    public String getFailureReason() { return failureReason; }

    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getVersion() { return version; }

    public void setVersion(Long version) { this.version = version; }
}
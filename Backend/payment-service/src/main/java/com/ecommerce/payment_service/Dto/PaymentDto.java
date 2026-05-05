package com.ecommerce.payment_service.Dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentDto(
        UUID id,
        String orderId,
        String userId,
        BigDecimal amount,
        String currency,
        String status,
        String paymentMethod,
        String transactionRef,
        String failureReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

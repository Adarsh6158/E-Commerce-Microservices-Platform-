package com.ecommerce.payment_service.Dto;

import com.ecommerce.payment_service.Domain.Payment;

public final class DtoMapper {

    private DtoMapper() {}

    public static PaymentDto toDto(Payment p) {
        return new PaymentDto(p.getId(), p.getOrderId(), p.getUserId(), p.getAmount(),
                p.getCurrency(), p.getStatus() != null ? p.getStatus().name() : null,
                p.getPaymentMethod() != null ? p.getPaymentMethod().name() : null,
                p.getTransactionRef(), p.getFailureReason(), p.getCreatedAt(), p.getUpdatedAt());
    }
}

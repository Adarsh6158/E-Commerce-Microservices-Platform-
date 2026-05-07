package com.ecommerce.pricing_service.DTO;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public record CreatePricingRuleRequest(
        String productId,
        String categoryId,
        @NotBlank String name,
        @NotBlank String ruleType,
        int priority,
        boolean active,
        Map<String, Object> conditions,
        Map<String, Object> discount,
        List<TierBracketInput> tiers,
        Instant validFrom,
        Instant validUntil
) {
    public record TierBracketInput(int minQuantity, int maxQuantity, BigDecimal pricePerUnit) {}
}

package com.ecommerce.pricing_service.DTO;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public record PricingRuleDto(
        String id,
        String productId,
        String categoryId,
        String name,
        String ruleType,
        int priority,
        boolean active,
        Map<String, Object> conditions,
        Map<String, Object> discount,
        List<TierBracketDto> tiers,
        Instant validFrom,
        Instant validUntil,
        Instant createdAt,
        Instant updatedAt
) {
    public record TierBracketDto(int minQuantity, int maxQuantity, BigDecimal pricePerUnit) {}
}

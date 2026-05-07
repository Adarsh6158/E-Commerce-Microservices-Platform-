package com.ecommerce.pricing_service.DTO;

import com.ecommerce.pricing_service.Domain.PricingRule;

import java.util.List;

public final class DtoMapper {

    private DtoMapper() {}

    public static PricingRuleDto toDto(PricingRule r) {
        List<PricingRuleDto.TierBracketDto> tiers = r.getTiers() != null
                ? r.getTiers().stream().map(t -> new PricingRuleDto.TierBracketDto(
                t.getMinQuantity(), t.getMaxQuantity(), t.getPricePerUnit())).toList()
                : List.of();

        return new PricingRuleDto(
                r.getId(), r.getProductId(), r.getCategoryId(), r.getName(),
                r.getRuleType(), r.getPriority(), r.isActive(), r.getConditions(), r.getDiscount(),
                tiers, r.getValidFrom(), r.getValidUntil(), r.getCreatedAt(), r.getUpdatedAt());
    }

    public static PricingRule toEntity(CreatePricingRuleRequest r) {
        PricingRule rule = new PricingRule();
        rule.setProductId(r.productId());
        rule.setCategoryId(r.categoryId());
        rule.setName(r.name());
        rule.setRuleType(r.ruleType());
        rule.setPriority(r.priority());
        rule.setActive(r.active());
        rule.setConditions(r.conditions());
        rule.setDiscount(r.discount());

        if (r.tiers() != null) {
            rule.setTiers(r.tiers().stream().map(t -> {
                PricingRule.TierBracket tb = new PricingRule.TierBracket();
                tb.setMinQuantity(t.minQuantity());
                tb.setMaxQuantity(t.maxQuantity());
                tb.setPricePerUnit(t.pricePerUnit());
                return tb;
            }).toList());
        }

        rule.setValidFrom(r.validFrom());
        rule.setValidUntil(r.validUntil());

        return rule;
    }
}

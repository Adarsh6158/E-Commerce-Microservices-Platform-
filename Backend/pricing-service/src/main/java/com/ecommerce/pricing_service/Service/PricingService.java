package com.ecommerce.pricing_service.Service;

import com.ecommerce.pricing_service.Domain.PricingRule;
import com.ecommerce.pricing_service.Repository.PricingRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Map;

@Service
public class PricingService {

    private static final Logger log = LoggerFactory.getLogger(PricingService.class);

    private final PricingRuleRepository ruleRepository;

    public PricingService(PricingRuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public Mono<Map<String, Object>> calculatePrice(String productId, BigDecimal basePrice, int quantity) {
        return ruleRepository.findByProductIdAndActiveTrueOrderByPriorityDesc(productId)
                .filter(rule -> isValid(rule))
                .next() // Take highest priority rule
                .map(rule -> applyRule(rule, basePrice, quantity))
                .defaultIfEmpty(Map.of(
                        "productId", productId,
                        "basePrice", basePrice,
                        "finalPrice", basePrice,
                        "quantity", quantity,
                        "totalPrice", basePrice.multiply(BigDecimal.valueOf(quantity)),
                        "discountApplied", false
                ))
                .doOnNext(result -> log.debug("Price calculated. productId={}, finalPrice={}", productId, result.get("finalPrice")));
    }

    public Mono<PricingRule> createRule(PricingRule rule) {
        rule.setCreatedAt(Instant.now());
        rule.setUpdatedAt(Instant.now());
        return ruleRepository.save(rule)
                .doOnSuccess(r -> log.info("Pricing rule created. id={}, type={}, productId={}",
                        r.getId(), r.getRuleType(), r.getProductId()));
    }

    public Mono<PricingRule> updateRule(String id, PricingRule updates) {
        return ruleRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Pricing rule not found: " + id)))
                .flatMap(existing -> {
                    if (updates.getName() != null) existing.setName(updates.getName());
                    if (updates.getRuleType() != null) existing.setRuleType(updates.getRuleType());
                    if (updates.getConditions() != null) existing.setConditions(updates.getConditions());
                    if (updates.getDiscount() != null) existing.setDiscount(updates.getDiscount());
                    if (updates.getTiers() != null) existing.setTiers(updates.getTiers());
                    if (updates.getValidFrom() != null) existing.setValidFrom(updates.getValidFrom());
                    if (updates.getValidUntil() != null) existing.setValidUntil(updates.getValidUntil());

                    existing.setActive(updates.isActive());
                    existing.setPriority(updates.getPriority());
                    existing.setUpdatedAt(Instant.now());

                    return ruleRepository.save(existing);
                });
    }

    public Flux<PricingRule> getRulesForProduct(String productId) {
        return ruleRepository.findByProductIdAndActiveTrueOrderByPriorityDesc(productId);
    }

    public Flux<PricingRule> getAllActiveRules() {
        return ruleRepository.findByActiveTrue();
    }

    public Mono<Void> deleteRule(String id) {
        return ruleRepository.deleteById(id);
    }

    private boolean isValid(PricingRule rule) {
        Instant now = Instant.now();
        if (rule.getValidFrom() != null && now.isBefore(rule.getValidFrom())) return false;
        if (rule.getValidUntil() != null && now.isAfter(rule.getValidUntil())) return false;
        return true;
    }

    private Map<String, Object> applyRule(PricingRule rule, BigDecimal basePrice, int quantity) {
        BigDecimal finalPrice = basePrice;

        if (rule.getDiscount() != null) {
            String discountType = (String) rule.getDiscount().getOrDefault("type", "NONE");
            Object rawValue = rule.getDiscount().get("value");
            BigDecimal discountValue = rawValue != null ? new BigDecimal(rawValue.toString()) : BigDecimal.ZERO;

            switch (discountType) {
                case "PERCENTAGE":
                    BigDecimal discountAmount = basePrice.multiply(discountValue)
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    finalPrice = basePrice.subtract(discountAmount);
                    break;

                case "FIXED":
                    finalPrice = basePrice.subtract(discountValue).max(BigDecimal.ZERO);
                    break;

                default:
                    break;
            }
        }

        if (rule.getTiers() != null && !rule.getTiers().isEmpty()) {
            for (PricingRule.TierBracket tier : rule.getTiers()) {
                if (quantity >= tier.getMinQuantity() &&
                        (tier.getMaxQuantity() == 0 || quantity <= tier.getMaxQuantity())) {
                    finalPrice = tier.getPricePerUnit();
                    break;
                }
            }
        }

        return Map.of(
                "productId", rule.getProductId() != null ? rule.getProductId() : "",
                "basePrice", basePrice,
                "finalPrice", finalPrice,
                "quantity", quantity,
                "totalPrice", finalPrice.multiply(BigDecimal.valueOf(quantity)),
                "discountApplied", true,
                "ruleName", rule.getName() != null ? rule.getName() : "",
                "ruleType", rule.getRuleType() != null ? rule.getRuleType() : ""
        );
    }
}

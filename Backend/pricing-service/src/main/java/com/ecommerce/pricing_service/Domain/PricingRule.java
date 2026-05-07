package com.ecommerce.pricing_service.Domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Document(collection = "pricing_rules")
public class PricingRule {

    @Id
    private String id;

    @Indexed
    private String productId;

    @Indexed
    private String categoryId;

    private String name;

    private String ruleType; // PERCENTAGE_DISCOUNT, FIXED_DISCOUNT, BOGO, TIERED, TIME_BASED

    private int priority; // Higher = evaluated first

    private boolean active;

    // Flexible rule configuration (varies by ruleType)
    private Map<String, Object> conditions; // e.g., {"minQuantity": 5, "userTier": "PREMIUM"}

    private Map<String, Object> discount; // e.g., {"type": "PERCENTAGE", "value": 10} or {"type": "FIXED", "value": 5.00}

    private List<TierBracket> tiers; // For TIERED pricing

    private Instant validFrom;
    private Instant validUntil;
    private Instant createdAt;
    private Instant updatedAt;

    public static class TierBracket {
        private int minQuantity;
        private int maxQuantity;
        private BigDecimal pricePerUnit;

        public int getMinQuantity() { return minQuantity; }
        public void setMinQuantity(int minQuantity) { this.minQuantity = minQuantity; }

        public int getMaxQuantity() { return maxQuantity; }
        public void setMaxQuantity(int maxQuantity) { this.maxQuantity = maxQuantity; }

        public BigDecimal getPricePerUnit() { return pricePerUnit; }
        public void setPricePerUnit(BigDecimal pricePerUnit) { this.pricePerUnit = pricePerUnit; }
    }

    public PricingRule() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRuleType() { return ruleType; }
    public void setRuleType(String ruleType) { this.ruleType = ruleType; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Map<String, Object> getConditions() { return conditions; }
    public void setConditions(Map<String, Object> conditions) { this.conditions = conditions; }

    public Map<String, Object> getDiscount() { return discount; }
    public void setDiscount(Map<String, Object> discount) { this.discount = discount; }

    public List<TierBracket> getTiers() { return tiers; }
    public void setTiers(List<TierBracket> tiers) { this.tiers = tiers; }

    public Instant getValidFrom() { return validFrom; }
    public void setValidFrom(Instant validFrom) { this.validFrom = validFrom; }

    public Instant getValidUntil() { return validUntil; }
    public void setValidUntil(Instant validUntil) { this.validUntil = validUntil; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}

package com.ecommerce.pricing_service.Repository;

import com.ecommerce.pricing_service.Domain.PricingRule;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface PricingRuleRepository extends ReactiveMongoRepository<PricingRule, String> {

    Flux<PricingRule> findByProductIdAndActiveTrueOrderByPriorityDesc(String productId);

    Flux<PricingRule> findByCategoryIdAndActiveTrueOrderByPriorityDesc(String categoryId);

    Flux<PricingRule> findByActiveTrue();
}

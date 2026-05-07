package com.ecommerce.pricing_service.Controller;

import com.ecommerce.pricing_service.DTO.CreatePricingRuleRequest;
import com.ecommerce.pricing_service.DTO.DtoMapper;
import com.ecommerce.pricing_service.DTO.PricingRuleDto;
import com.ecommerce.pricing_service.Service.PricingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/pricing")
public class PricingController {

    private final PricingService pricingService;

    public PricingController(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    @GetMapping("/calculate")
    public Mono<Map<String, Object>> calculatePrice(
            @RequestParam String productId,
            @RequestParam(required = false) BigDecimal basePrice,
            @RequestParam(defaultValue = "1") int quantity) {

        BigDecimal price = basePrice != null ? basePrice : BigDecimal.ZERO;
        return pricingService.calculatePrice(productId, price, quantity);
    }

    @GetMapping("/rules/product/{productId}")
    public Flux<PricingRuleDto> getRulesForProduct(@PathVariable String productId) {
        return pricingService.getRulesForProduct(productId).map(DtoMapper::toDto);
    }

    @GetMapping("/rules")
    public Flux<PricingRuleDto> getAllActiveRules() {
        return pricingService.getAllActiveRules().map(DtoMapper::toDto);
    }

    @PostMapping("/rules")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PricingRuleDto> createRule(@Valid @RequestBody CreatePricingRuleRequest request) {
        return pricingService.createRule(DtoMapper.toEntity(request)).map(DtoMapper::toDto);
    }

    @PutMapping("/rules/{id}")
    public Mono<PricingRuleDto> updateRule(@PathVariable String id,
                                           @RequestBody CreatePricingRuleRequest request) {
        return pricingService.updateRule(id, DtoMapper.toEntity(request)).map(DtoMapper::toDto);
    }

    @DeleteMapping("/rules/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteRule(@PathVariable String id) {
        return pricingService.deleteRule(id);
    }
}

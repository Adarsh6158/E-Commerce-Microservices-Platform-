package com.ecommerce.search_service.Validator;

import com.ecommerce.search_service.Config.SearchProperties;
import com.ecommerce.search_service.Exception.InvalidSearchRequestException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class SearchRequestValidator {

    private final SearchProperties searchProperties;

    public SearchRequestValidator(SearchProperties searchProperties) {
        this.searchProperties = searchProperties;
    }

    public int validateAndClampSize(int size) {
        if (size < 1) {
            throw new InvalidSearchRequestException("Page size must be at least 1");
        }
        return Math.min(size, searchProperties.getMaxPageSize());
    }

    public void validatePage(int page) {
        if (page < 0) {
            throw new InvalidSearchRequestException("Page number must be non-negative");
        }
    }

    public void validatePriceRange(BigDecimal min, BigDecimal max) {
        if (min != null && min.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidSearchRequestException("Minimum price must be non-negative");
        }
        if (max != null && max.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidSearchRequestException("Maximum price must be non-negative");
        }
        if (min != null && max != null && min.compareTo(max) > 0) {
            throw new InvalidSearchRequestException("Minimum price cannot exceed maximum price");
        }
    }

    public void validateQuery(String query) {
        if (query == null || query.isBlank()) {
            throw new InvalidSearchRequestException("Search query must not be empty");
        }
    }

    public int clampRecommendationSize(int size) {
        if (size < 1) {
            return 1;
        }
        return Math.min(size, searchProperties.getMaxRecommendations());
    }
}

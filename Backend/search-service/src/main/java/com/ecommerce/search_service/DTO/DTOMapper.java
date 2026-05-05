package com.ecommerce.search_service.DTO;

import com.ecommerce.search_service.Domain.ProductDocument;

public final class DTOMapper {

    private DTOMapper() {}

    public static ProductSearchResult toDto(ProductDocument d) {
        return new ProductSearchResult(
                d.getId(),
                d.getName(),
                d.getDescription(),
                d.getSku(),
                d.getBrand(),
                d.getCategoryId(),
                d.getCategoryName(),
                d.getBasePrice(),
                d.getImageUrl(),
                d.isActive(),
                d.getAttributes(),
                d.getUpdatedAt()
        );
    }
}

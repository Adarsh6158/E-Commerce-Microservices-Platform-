package com.ecommerce.catalog_service.Dto;

import com.ecommerce.catalog_service.Domain.Category;
import com.ecommerce.catalog_service.Domain.Product;
import com.ecommerce.catalog_service.Domain.Review;

public final class DtoMapper {

    private DtoMapper() {}

    public static ProductDto toDto(Product p) {
        return new ProductDto(
                p.getId(),
                p.getSku(),
                p.getName(),
                p.getDescription(),
                p.getCategoryId(),
                p.getBrand(),
                p.getBasePrice(),
                p.getImageUrl(),
                p.getImageUrls(),
                p.isActive(),
                p.getWeight(),
                p.getAttributes(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }

    public static Product toEntity(CreateProductRequest r) {
        Product p = new Product();
        p.setSku(r.sku());
        p.setName(r.name());
        p.setDescription(r.description());
        p.setCategoryId(r.categoryId());
        p.setBrand(r.brand());
        p.setBasePrice(r.basePrice());
        p.setImageUrl(r.imageUrl());
        p.setWeight(r.weight());
        p.setAttributes(r.attributes());
        return p;
    }

    public static CategoryDto toDto(Category c) {
        return new CategoryDto(
                c.getId(),
                c.getName(),
                c.getSlug(),
                c.getParentId(),
                c.getDescription(),
                c.isActive(),
                c.getCreatedAt()
        );
    }

    public static Category toEntity(CreateCategoryRequest r) {
        Category c = new Category();
        c.setName(r.name());
        c.setSlug(r.slug());
        c.setParentId(r.parentId());
        c.setDescription(r.description());
        return c;
    }

    public static ReviewDto toReviewDto(Review r) {
        return new ReviewDto(
                r.getId(),
                r.getProductId(),
                r.getUserId(),
                r.getUserName(),
                r.getRating(),
                r.getTitle(),
                r.getComment(),
                r.isVerified(),
                r.getCreatedAt()
        );
    }
}

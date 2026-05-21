package com.ecommerce.search_service.Mapper;

import com.ecommerce.search_service.Domain.ProductDocument;
import com.ecommerce.search_service.Dto.Event.ProductEventPayload;
import com.ecommerce.search_service.Dto.Response.ProductSearchResult;
import com.ecommerce.search_service.Dto.Response.SuggestionResult;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ProductSearchMapperImpl implements ProductSearchMapper {

    @Override
    public ProductSearchResult toSearchResult(ProductDocument d) {
        return new ProductSearchResult(
                d.getId(),
                d.getName(),
                d.getDescription(),
                d.getSku(),
                d.getBrand(),
                d.getCategoryId(),
                d.getCategoryName(),
                d.getBasePrice(),
                d.getImage(),
                d.getThumbnail(),
                d.getGalleryImages(),
                d.getAltText(),
                d.isActive(),
                d.getAttributes(),
                d.getUpdatedAt()
        );
    }

    @Override
    public SuggestionResult toSuggestionResult(ProductDocument d) {
        return new SuggestionResult(
                d.getId(),
                d.getName(),
                d.getBrand() != null ? d.getBrand() : ""
        );
    }

    @Override
    public ProductDocument toDocument(ProductEventPayload payload) {
        ProductDocument doc = new ProductDocument();
        doc.setId(payload.getProductId());
        doc.setSku(payload.getSku());
        doc.setName(payload.getName());
        doc.setDescription(payload.getDescription());
        doc.setBrand(payload.getBrand());
        doc.setCategoryId(payload.getCategoryId());
        doc.setCategoryName(payload.getCategoryName());
        doc.setBasePrice(payload.getBasePrice());
        doc.setImage(payload.getImage());
        doc.setThumbnail(payload.getThumbnail());
        doc.setGalleryImages(payload.getGalleryImages());
        doc.setAltText(payload.getAltText());
        doc.setActive(payload.isActive());
        doc.setUpdatedAt(Instant.now());
        return doc;
    }
}

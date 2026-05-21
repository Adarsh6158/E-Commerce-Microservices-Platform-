package com.ecommerce.search_service.Mapper;

import com.ecommerce.search_service.Domain.ProductDocument;
import com.ecommerce.search_service.Dto.Event.ProductEventPayload;
import com.ecommerce.search_service.Dto.Response.ProductSearchResult;
import com.ecommerce.search_service.Dto.Response.SuggestionResult;

public interface ProductSearchMapper {

    ProductSearchResult toSearchResult(ProductDocument document);

    SuggestionResult toSuggestionResult(ProductDocument document);

    ProductDocument toDocument(ProductEventPayload payload);
}

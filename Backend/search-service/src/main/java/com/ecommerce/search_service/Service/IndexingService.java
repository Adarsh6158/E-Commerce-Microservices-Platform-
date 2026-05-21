package com.ecommerce.search_service.Service;

import com.ecommerce.search_service.Domain.ProductDocument;
import reactor.core.publisher.Mono;

public interface IndexingService {

    Mono<ProductDocument> indexProduct(ProductDocument document);

    Mono<Void> deleteProduct(String productId);

    Mono<Long> reindexAll();
}

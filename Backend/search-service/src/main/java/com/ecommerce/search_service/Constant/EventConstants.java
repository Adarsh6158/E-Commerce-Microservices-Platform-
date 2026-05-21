package com.ecommerce.search_service.Constant;

public final class EventConstants {

    private EventConstants() {
      
    }
    public static final String TOPIC_PRODUCT_CREATED = "catalog.product.created";
    public static final String TOPIC_PRODUCT_UPDATED = "catalog.product.updated";
    public static final String TOPIC_PRODUCT_DELETED = "catalog.product.deleted";
    public static final String GROUP_INDEXER = "search-service-indexer";
}

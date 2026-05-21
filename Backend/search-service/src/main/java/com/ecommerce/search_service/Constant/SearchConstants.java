package com.ecommerce.search_service.Constant;

public final class SearchConstants {

    private SearchConstants() {
      
    }

    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_BRAND = "brand";
    public static final String FIELD_ACTIVE = "active";
    public static final String FIELD_BASE_PRICE = "basePrice";
    public static final String FIELD_CATEGORY_ID = "categoryId";
    public static final String FIELD_NAME_BOOSTED = "name^3";
    public static final String FIELD_BRAND_BOOSTED = "brand^2";
    public static final String FIELD_NAME_2GRAM = "name._2gram";
    public static final String FIELD_NAME_3GRAM = "name._3gram";
    public static final String FUZZINESS_AUTO = "AUTO";
    public static final String RESPONSE_STATUS = "status";
    public static final String RESPONSE_COMPLETED = "completed";
    public static final String RESPONSE_INDEXED = "indexed";
}

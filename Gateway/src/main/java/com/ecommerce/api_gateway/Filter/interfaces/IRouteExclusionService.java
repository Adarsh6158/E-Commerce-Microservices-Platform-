package com.ecommerce.api_gateway.Filter.interfaces;

public interface IRouteExclusionService {
    boolean isExcluded(String path);
}

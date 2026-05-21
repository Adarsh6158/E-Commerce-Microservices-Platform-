package com.ecommerce.search_service.Config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "catalog")
public class CatalogProperties {

    private String host = "localhost";
    private int port = 8082;
    private String productsPath = "/products";
    private int connectTimeoutMs = 5000;
    private int readTimeoutMs = 10000;

    public String getBaseUrl() {
        return "http://" + host + ":" + port;
    }
}

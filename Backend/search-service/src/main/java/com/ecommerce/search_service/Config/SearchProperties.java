package com.ecommerce.search_service.Config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "search")
public class SearchProperties {

    private int defaultPageSize = 20;
    private int maxPageSize = 100;
    private int suggestLimit = 8;
    private int maxRecommendations = 20;
}

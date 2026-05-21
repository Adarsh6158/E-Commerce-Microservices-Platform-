package com.ecommerce.search_service.Dto.Event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductEventPayload {

    private String eventType;
    private String productId;
    private String sku;
    private String name;
    private String description;
    private String brand;
    private String categoryId;
    private String categoryName;
    private BigDecimal basePrice;
    private String image;
    private String thumbnail;
    private List<String> galleryImages;
    private String altText;
    @Builder.Default
    private boolean active = true;
}

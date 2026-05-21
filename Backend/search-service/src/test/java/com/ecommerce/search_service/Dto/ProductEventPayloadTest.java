package com.ecommerce.search_service.Dto;

import com.ecommerce.search_service.Dto.Event.ProductEventPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductEventPayloadTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("JSON Deserialization")
    class DeserializationTests {

        @Test
        @DisplayName("should deserialize full payload correctly")
        void fullPayload() throws Exception {
            String json = """
                    {
                        "eventType": "PRODUCT_CREATED",
                        "productId": "prod-1",
                        "sku": "SKU-001",
                        "name": "Test Product",
                        "description": "A test description",
                        "brand": "TestBrand",
                        "categoryId": "cat-1",
                        "categoryName": "Electronics",
                        "basePrice": 99.99,
                        "image": "img.jpg",
                        "thumbnail": "thumb.jpg",
                        "galleryImages": ["g1.jpg", "g2.jpg"],
                        "altText": "Alt text",
                        "active": true
                    }
                    """;

            ProductEventPayload payload = objectMapper.readValue(json, ProductEventPayload.class);

            assertThat(payload.getEventType()).isEqualTo("PRODUCT_CREATED");
            assertThat(payload.getProductId()).isEqualTo("prod-1");
            assertThat(payload.getSku()).isEqualTo("SKU-001");
            assertThat(payload.getName()).isEqualTo("Test Product");
            assertThat(payload.getDescription()).isEqualTo("A test description");
            assertThat(payload.getBrand()).isEqualTo("TestBrand");
            assertThat(payload.getCategoryId()).isEqualTo("cat-1");
            assertThat(payload.getCategoryName()).isEqualTo("Electronics");
            assertThat(payload.getBasePrice()).isEqualByComparingTo(BigDecimal.valueOf(99.99));
            assertThat(payload.getImage()).isEqualTo("img.jpg");
            assertThat(payload.getThumbnail()).isEqualTo("thumb.jpg");
            assertThat(payload.getGalleryImages()).containsExactly("g1.jpg", "g2.jpg");
            assertThat(payload.getAltText()).isEqualTo("Alt text");
            assertThat(payload.isActive()).isTrue();
        }

        @Test
        @DisplayName("should deserialize minimal payload with defaults")
        void minimalPayload() throws Exception {
            String json = """
                    {
                        "productId": "prod-2",
                        "name": "Minimal"
                    }
                    """;

            ProductEventPayload payload = objectMapper.readValue(json, ProductEventPayload.class);

            assertThat(payload.getProductId()).isEqualTo("prod-2");
            assertThat(payload.getName()).isEqualTo("Minimal");
            assertThat(payload.getBrand()).isNull();
            assertThat(payload.getBasePrice()).isNull();
            assertThat(payload.getGalleryImages()).isNull();
            assertThat(payload.isActive()).isTrue();
        }

        @Test
        @DisplayName("should ignore unknown fields")
        void unknownFields() throws Exception {
            String json = """
                    {
                        "productId": "prod-3",
                        "name": "Product",
                        "unknownField": "value",
                        "anotherUnknown": 42
                    }
                    """;

            ProductEventPayload payload = objectMapper.readValue(json, ProductEventPayload.class);

            assertThat(payload.getProductId()).isEqualTo("prod-3");
            assertThat(payload.getName()).isEqualTo("Product");
        }

        @Test
        @DisplayName("should handle null values in JSON")
        void nullValues() throws Exception {
            String json = """
                    {
                        "productId": "prod-4",
                        "name": null,
                        "brand": null,
                        "basePrice": null
                    }
                    """;

            ProductEventPayload payload = objectMapper.readValue(json, ProductEventPayload.class);

            assertThat(payload.getProductId()).isEqualTo("prod-4");
            assertThat(payload.getName()).isNull();
            assertThat(payload.getBrand()).isNull();
            assertThat(payload.getBasePrice()).isNull();
        }

        @Test
        @DisplayName("should handle empty gallery images array")
        void emptyGalleryImages() throws Exception {
            String json = """
                    {
                        "productId": "prod-5",
                        "galleryImages": []
                    }
                    """;

            ProductEventPayload payload = objectMapper.readValue(json, ProductEventPayload.class);

            assertThat(payload.getGalleryImages()).isEmpty();
        }

        @Test
        @DisplayName("should handle inactive product flag")
        void inactiveProduct() throws Exception {
            String json = """
                    {
                        "productId": "prod-6",
                        "active": false
                    }
                    """;

            ProductEventPayload payload = objectMapper.readValue(json, ProductEventPayload.class);

            assertThat(payload.isActive()).isFalse();
        }

        @Test
        @DisplayName("should handle price as string number")
        void priceAsNumber() throws Exception {
            String json = """
                    {
                        "productId": "prod-7",
                        "basePrice": 1234.56
                    }
                    """;

            ProductEventPayload payload = objectMapper.readValue(json, ProductEventPayload.class);

            assertThat(payload.getBasePrice()).isEqualByComparingTo(new BigDecimal("1234.56"));
        }
    }

    @Nested
    @DisplayName("Builder")
    class BuilderTests {

        @Test
        @DisplayName("should build payload with all fields")
        void buildFullPayload() {
            ProductEventPayload payload = ProductEventPayload.builder()
                    .eventType("PRODUCT_CREATED")
                    .productId("prod-1")
                    .name("Built Product")
                    .brand("BuilderBrand")
                    .basePrice(BigDecimal.TEN)
                    .active(true)
                    .build();

            assertThat(payload.getEventType()).isEqualTo("PRODUCT_CREATED");
            assertThat(payload.getProductId()).isEqualTo("prod-1");
            assertThat(payload.getName()).isEqualTo("Built Product");
            assertThat(payload.getBrand()).isEqualTo("BuilderBrand");
            assertThat(payload.getBasePrice()).isEqualTo(BigDecimal.TEN);
            assertThat(payload.isActive()).isTrue();
        }

        @Test
        @DisplayName("should default active to true")
        void defaultActiveTrue() {
            ProductEventPayload payload = ProductEventPayload.builder()
                    .productId("prod-2")
                    .build();

            assertThat(payload.isActive()).isTrue();
        }
    }
}

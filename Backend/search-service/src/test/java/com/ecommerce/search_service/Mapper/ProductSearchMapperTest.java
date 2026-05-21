package com.ecommerce.search_service.Mapper;

import com.ecommerce.search_service.Domain.ProductDocument;
import com.ecommerce.search_service.Dto.Event.ProductEventPayload;
import com.ecommerce.search_service.Dto.Response.ProductSearchResult;
import com.ecommerce.search_service.Dto.Response.SuggestionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductSearchMapperTest {

    private ProductSearchMapperImpl mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductSearchMapperImpl();
    }

    private ProductDocument createFullProduct() {
        ProductDocument doc = new ProductDocument();
        doc.setId("prod-1");
        doc.setName("Wireless Headphones");
        doc.setDescription("Premium wireless headphones with ANC");
        doc.setSku("WH-1000XM5");
        doc.setBrand("Sony");
        doc.setCategoryId("cat-audio");
        doc.setCategoryName("Audio");
        doc.setBasePrice(BigDecimal.valueOf(349.99));
        doc.setImage("https://example.com/image.jpg");
        doc.setThumbnail("https://example.com/thumb.jpg");
        doc.setGalleryImages(List.of("https://example.com/g1.jpg", "https://example.com/g2.jpg"));
        doc.setAltText("Sony WH-1000XM5");
        doc.setActive(true);
        doc.setAttributes(null);
        doc.setUpdatedAt(Instant.parse("2024-01-15T10:30:00Z"));
        return doc;
    }

    @Nested
    @DisplayName("toSearchResult()")
    class ToSearchResultTests {

        @Test
        @DisplayName("should map all fields correctly")
        void mapAllFields() {
            ProductDocument doc = createFullProduct();
            ProductSearchResult result = mapper.toSearchResult(doc);

            assertThat(result.id()).isEqualTo("prod-1");
            assertThat(result.name()).isEqualTo("Wireless Headphones");
            assertThat(result.description()).isEqualTo("Premium wireless headphones with ANC");
            assertThat(result.sku()).isEqualTo("WH-1000XM5");
            assertThat(result.brand()).isEqualTo("Sony");
            assertThat(result.categoryId()).isEqualTo("cat-audio");
            assertThat(result.categoryName()).isEqualTo("Audio");
            assertThat(result.basePrice()).isEqualTo(BigDecimal.valueOf(349.99));
            assertThat(result.image()).isEqualTo("https://example.com/image.jpg");
            assertThat(result.thumbnail()).isEqualTo("https://example.com/thumb.jpg");
            assertThat(result.galleryImages()).hasSize(2);
            assertThat(result.altText()).isEqualTo("Sony WH-1000XM5");
            assertThat(result.active()).isTrue();
            assertThat(result.attributes()).isNull();
            assertThat(result.updatedAt()).isEqualTo(Instant.parse("2024-01-15T10:30:00Z"));
        }

        @Test
        @DisplayName("should handle null optional fields")
        void mapNullFields() {
            ProductDocument doc = new ProductDocument();
            doc.setId("prod-2");
            doc.setName("Minimal");

            ProductSearchResult result = mapper.toSearchResult(doc);

            assertThat(result.id()).isEqualTo("prod-2");
            assertThat(result.name()).isEqualTo("Minimal");
            assertThat(result.brand()).isNull();
            assertThat(result.description()).isNull();
            assertThat(result.basePrice()).isNull();
            assertThat(result.galleryImages()).isNull();
        }

        @Test
        @DisplayName("should handle inactive product")
        void mapInactiveProduct() {
            ProductDocument doc = createFullProduct();
            doc.setActive(false);

            ProductSearchResult result = mapper.toSearchResult(doc);

            assertThat(result.active()).isFalse();
        }

        @Test
        @DisplayName("should handle empty gallery images")
        void mapEmptyGalleryImages() {
            ProductDocument doc = createFullProduct();
            doc.setGalleryImages(List.of());

            ProductSearchResult result = mapper.toSearchResult(doc);

            assertThat(result.galleryImages()).isEmpty();
        }
    }

    @Nested
    @DisplayName("toSuggestionResult()")
    class ToSuggestionResultTests {

        @Test
        @DisplayName("should map id, name, brand correctly")
        void mapSuggestion() {
            ProductDocument doc = createFullProduct();
            SuggestionResult result = mapper.toSuggestionResult(doc);

            assertThat(result.id()).isEqualTo("prod-1");
            assertThat(result.name()).isEqualTo("Wireless Headphones");
            assertThat(result.brand()).isEqualTo("Sony");
        }

        @Test
        @DisplayName("should return empty string for null brand")
        void mapNullBrand() {
            ProductDocument doc = new ProductDocument();
            doc.setId("prod-2");
            doc.setName("No Brand Product");
            doc.setBrand(null);

            SuggestionResult result = mapper.toSuggestionResult(doc);

            assertThat(result.brand()).isEqualTo("");
        }

        @Test
        @DisplayName("should handle product with only id")
        void mapMinimalProduct() {
            ProductDocument doc = new ProductDocument();
            doc.setId("prod-3");

            SuggestionResult result = mapper.toSuggestionResult(doc);

            assertThat(result.id()).isEqualTo("prod-3");
            assertThat(result.name()).isNull();
            assertThat(result.brand()).isEqualTo("");
        }
    }

    @Nested
    @DisplayName("toDocument()")
    class ToDocumentTests {

        @Test
        @DisplayName("should map event payload to document with all fields")
        void mapFullPayload() {
            ProductEventPayload payload = ProductEventPayload.builder()
                    .eventType("PRODUCT_CREATED")
                    .productId("prod-1")
                    .sku("SKU-001")
                    .name("Test Product")
                    .description("A test product")
                    .brand("TestBrand")
                    .categoryId("cat-1")
                    .categoryName("Category One")
                    .basePrice(BigDecimal.valueOf(29.99))
                    .image("https://example.com/img.jpg")
                    .thumbnail("https://example.com/thumb.jpg")
                    .galleryImages(List.of("https://example.com/g1.jpg"))
                    .altText("Test alt")
                    .active(true)
                    .build();

            ProductDocument doc = mapper.toDocument(payload);

            assertThat(doc.getId()).isEqualTo("prod-1");
            assertThat(doc.getSku()).isEqualTo("SKU-001");
            assertThat(doc.getName()).isEqualTo("Test Product");
            assertThat(doc.getDescription()).isEqualTo("A test product");
            assertThat(doc.getBrand()).isEqualTo("TestBrand");
            assertThat(doc.getCategoryId()).isEqualTo("cat-1");
            assertThat(doc.getCategoryName()).isEqualTo("Category One");
            assertThat(doc.getBasePrice()).isEqualTo(BigDecimal.valueOf(29.99));
            assertThat(doc.getImage()).isEqualTo("https://example.com/img.jpg");
            assertThat(doc.getThumbnail()).isEqualTo("https://example.com/thumb.jpg");
            assertThat(doc.getGalleryImages()).hasSize(1);
            assertThat(doc.getAltText()).isEqualTo("Test alt");
            assertThat(doc.isActive()).isTrue();
            assertThat(doc.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("should handle payload with null optional fields")
        void mapMinimalPayload() {
            ProductEventPayload payload = ProductEventPayload.builder()
                    .productId("prod-2")
                    .name("Minimal Product")
                    .active(true)
                    .build();

            ProductDocument doc = mapper.toDocument(payload);

            assertThat(doc.getId()).isEqualTo("prod-2");
            assertThat(doc.getName()).isEqualTo("Minimal Product");
            assertThat(doc.getBrand()).isNull();
            assertThat(doc.getBasePrice()).isNull();
            assertThat(doc.getGalleryImages()).isNull();
            assertThat(doc.isActive()).isTrue();
        }

        @Test
        @DisplayName("should map inactive product from event")
        void mapInactivePayload() {
            ProductEventPayload payload = ProductEventPayload.builder()
                    .productId("prod-3")
                    .name("Inactive Product")
                    .active(false)
                    .build();

            ProductDocument doc = mapper.toDocument(payload);

            assertThat(doc.isActive()).isFalse();
        }

        @Test
        @DisplayName("should set updatedAt to current time")
        void mapSetsUpdatedAt() {
            Instant before = Instant.now();

            ProductEventPayload payload = ProductEventPayload.builder()
                    .productId("prod-4")
                    .name("Timed Product")
                    .build();

            ProductDocument doc = mapper.toDocument(payload);

            assertThat(doc.getUpdatedAt()).isAfterOrEqualTo(before);
            assertThat(doc.getUpdatedAt()).isBeforeOrEqualTo(Instant.now());
        }
    }
}

package com.ecommerce.search_service.Domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.math.BigDecimal;
import java.time.Instant;

@Document(indexName = "products")
@Setting(replicas = 0, shards = 1)
public class ProductDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @Field(type = FieldType.Keyword)
    private String sku;

    @Field(type = FieldType.Keyword)
    private String brand;

    @Field(type = FieldType.Keyword)
    private String categoryId;

    @Field(type = FieldType.Keyword)
    private String categoryName;

    @Field(type = FieldType.Double)
    private BigDecimal basePrice;

    @Field(type = FieldType.Keyword)
    private String image;

    @Field(type = FieldType.Keyword)
    private String thumbnail;

    @Field(type = FieldType.Keyword)
    private java.util.List<String> galleryImages;

    @Field(type = FieldType.Keyword)
    private String altText;

    @Field(type = FieldType.Boolean)
    private boolean active;

    @Field(type = FieldType.Object)
    private Object attributes;

    @Field(type = FieldType.Date)
    private Instant updatedAt;

    public ProductDocument() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getThumbnail() { return thumbnail; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }

    public java.util.List<String> getGalleryImages() { return galleryImages; }
    public void setGalleryImages(java.util.List<String> galleryImages) { this.galleryImages = galleryImages; }

    public String getAltText() { return altText; }
    public void setAltText(String altText) { this.altText = altText; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Object getAttributes() { return attributes; }
    public void setAttributes(Object attributes) { this.attributes = attributes; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}

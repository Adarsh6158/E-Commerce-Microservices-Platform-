package com.ecommerce.catalog_service.Controller;

import com.ecommerce.catalog_service.Service.ImageStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/products")
public class ImageUploadController {

    private final ImageStorageService imageStorageService;

    public ImageUploadController(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    @PostMapping(value = "/{productId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Map<String, Object>> uploadImages(
            @PathVariable String productId,
            @RequestPart("files") Flux<FilePart> files) {

        return imageStorageService.storeImages(productId, files)
                .collectList()
                .map(urls -> Map.of("productId", productId, "imageUrls", urls));
    }
}

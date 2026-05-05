package com.ecommerce.catalog_service.Controller;

import com.ecommerce.catalog_service.Service.ImageStorageService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

@RestController
@RequestMapping("/products/images")
public class ImageController {

    private final ImageStorageService imageStorageService;

    public ImageController(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    @GetMapping("/{filename}")
    public Mono<ResponseEntity<Resource>> getImage(@PathVariable String filename) {

        if (!filename.matches("[a-zA-Z0-9._-]+\\.[a-zA-Z]+")) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        return Mono.fromCallable(() -> {
            Path path = imageStorageService.resolveImage(filename);
            if (!Files.exists(path)) {
                return ResponseEntity.notFound().<Resource>build();
            }
            String contentType = Files.probeContentType(path);
            MediaType mediaType = contentType != null ? MediaType.parseMediaType(contentType) : MediaType.APPLICATION_OCTET_STREAM;
            Resource resource = new FileSystemResource(path);
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .cacheControl(CacheControl.maxAge(Duration.ofDays(7)).cachePublic())
                    .body(resource);
        });
    }
}

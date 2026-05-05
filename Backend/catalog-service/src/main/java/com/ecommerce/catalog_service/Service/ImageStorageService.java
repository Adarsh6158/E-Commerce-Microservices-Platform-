package com.ecommerce.catalog_service.Service;

import com.ecommerce.catalog_service.Repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class ImageStorageService {

    private static final Logger log = LoggerFactory.getLogger(ImageStorageService.class);

    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp", "image/gif");

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    private final Path uploadDir;
    private final String baseUrl;
    private final ProductRepository productRepository;

    public ImageStorageService(
            @Value("${app.images.upload-dir:./uploads/products}") String uploadDirStr,
            @Value("${app.images.base-url:http://localhost:8082/products/images}") String baseUrl,
            ProductRepository productRepository
    ) {
        this.uploadDir = Paths.get(uploadDirStr).toAbsolutePath().normalize();
        this.baseUrl = baseUrl;
        this.productRepository = productRepository;

        try {
            Files.createDirectories(this.uploadDir);
            log.info("Image upload directory: {}", this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Cannot create upload directory: " + this.uploadDir, e);
        }
    }

    // ================= PUBLIC API =================

    public Flux<String> storeImages(String productId, Flux<FilePart> files) {
        return files
                .flatMap(file -> storeOne(productId, file))
                .collectList()
                .flatMapMany(urls ->
                        updateProductImages(productId, urls)
                                .thenMany(Flux.fromIterable(urls))
                );
    }

    // ================= CORE LOGIC =================

    private Mono<String> storeOne(String productId, FilePart filePart) {

        // 1. Validate content type
        String contentType = filePart.headers().getContentType() != null
                ? filePart.headers().getContentType().toString()
                : "";

        if (!ALLOWED_TYPES.contains(contentType)) {
            return Mono.error(new IllegalArgumentException("Unsupported file type: " + contentType));
        }

        // 2. Extract filename + extension safely
        String originalName = Optional.ofNullable(filePart.filename()).orElse("file");
        String ext = originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf("."))
                : ".jpg";

        // 3. Generate unique filename (IMPORTANT: collision-safe)
        String filename = productId + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;

        Path target = uploadDir.resolve(filename).normalize();

        // 4. Path traversal protection (CRITICAL)
        if (!target.startsWith(uploadDir)) {
            return Mono.error(new IllegalArgumentException("Invalid file path"));
        }

        // 5. Save file (reactive)
        return filePart.transferTo(target)
                .then(Mono.fromCallable(() -> {
                    String url = baseUrl + "/" + filename;
                    log.info("Image stored: {} -> {}", originalName, url);
                    return url;
                }));
    }

    // ================= DB UPDATE =================

    private Mono<Void> updateProductImages(String productId, List<String> newUrls) {
        return productRepository.findById(productId)
                .flatMap(product -> {

                    List<String> existing = product.getImageUrls() != null
                            ? new ArrayList<>(product.getImageUrls())
                            : new ArrayList<>();

                    existing.addAll(newUrls);
                    product.setImageUrls(existing);

                    // set primary image if missing
                    if (product.getImageUrl() == null && !newUrls.isEmpty()) {
                        product.setImageUrl(newUrls.get(0));
                    }

                    return productRepository.save(product);
                })
                .then();
    }

    // ================= HELPER =================

    public Path resolveImage(String filename) {
        Path resolved = uploadDir.resolve(filename).normalize();

        if (!resolved.startsWith(uploadDir)) {
            throw new IllegalArgumentException("Invalid path");
        }

        return resolved;
    }
}

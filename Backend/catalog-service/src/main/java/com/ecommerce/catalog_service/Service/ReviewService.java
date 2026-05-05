package com.ecommerce.catalog_service.Service;

import com.ecommerce.catalog_service.Domain.Review;
import com.ecommerce.catalog_service.Dto.CreateReviewRequest;
import com.ecommerce.catalog_service.Repository.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@Service
public class ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;
    private final WebClient webClient;

    @Value("${order.service.host:localhost}")
    private String orderServiceHost;

    @Value("${order.service.port:8087}")
    private int orderServicePort;

    public ReviewService(ReviewRepository reviewRepository, WebClient.Builder webClientBuilder) {
        this.reviewRepository = reviewRepository;
        this.webClient = webClientBuilder.build();
    }

    // ================= CREATE REVIEW =================

    public Mono<Review> createReview(String productId,
                                     String userId,
                                     String userName,
                                     CreateReviewRequest request) {

        return reviewRepository.findByProductIdAndUserId(productId, userId)
                .flatMap(existing ->
                        Mono.<Review>error(new IllegalStateException("You have already reviewed this product"))
                )
                .switchIfEmpty(
                        Mono.defer(() -> verifyPurchase(userId, productId))
                                .flatMap(purchased -> {

                                    Review review = new Review();
                                    review.setProductId(productId);
                                    review.setUserId(userId);
                                    review.setUserName(userName);
                                    review.setRating(request.rating());
                                    review.setTitle(request.title());
                                    review.setComment(request.comment());
                                    review.setVerified(purchased);
                                    review.setCreatedAt(Instant.now());

                                    return reviewRepository.save(review);
                                })
                )
                .doOnSuccess(r ->
                        log.info("Review created: productId={}, userId={}, verified={}",
                                productId, userId, r != null && r.isVerified())
                );
    }

    // ================= VERIFY PURCHASE =================

    private Mono<Boolean> verifyPurchase(String userId, String productId) {

        return webClient.get()
                .uri("http://{host}:{port}/orders/verify-purchase?userId={userId}&productId={productId}",
                        orderServiceHost, orderServicePort, userId, productId)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> Boolean.TRUE.equals(response.get("purchased")))
                .onErrorReturn(false) // fail-safe
                .doOnNext(result ->
                        log.debug("Purchase verification: userId={}, productId={}, purchased={}",
                                userId, productId, result)
                );
    }

    // ================= GET REVIEWS =================

    public Flux<Review> getReviewsByProduct(String productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    // ================= RATING AGGREGATION =================

    public Mono<Map<String, Object>> getProductRating(String productId) {

        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId)
                .collectList()
                .map(reviews -> {

                    if (reviews.isEmpty()) {
                        return Map.of(
                                "productId", productId,
                                "averageRating", 0.0,
                                "reviewCount", 0L
                        );
                    }

                    double avg = reviews.stream()
                            .mapToInt(Review::getRating)
                            .average()
                            .orElse(0.0);

                    return Map.of(
                            "productId", productId,
                            "averageRating", Math.round(avg * 10.0) / 10.0,
                            "reviewCount", (long) reviews.size()
                    );
                });
    }

    // ================= DELETE REVIEW =================

    public Mono<Void> deleteReview(String reviewId, String userId) {

        return reviewRepository.findById(reviewId)
                .filter(r -> r.getUserId().equals(userId))
                .switchIfEmpty(
                        Mono.error(new IllegalArgumentException("Review not found or not authorized"))
                )
                .flatMap(reviewRepository::delete);
    }
}

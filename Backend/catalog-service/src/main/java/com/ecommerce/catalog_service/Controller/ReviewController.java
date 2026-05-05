package com.ecommerce.catalog_service.Controller;

import com.ecommerce.catalog_service.Dto.CreateReviewRequest;
import com.ecommerce.catalog_service.Dto.DtoMapper;
import com.ecommerce.catalog_service.Dto.ReviewDto;
import com.ecommerce.catalog_service.Service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/products/{productId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ReviewDto> createReview(
            @PathVariable String productId,
            @Valid @RequestBody CreateReviewRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "anonymous") String userId,
            @RequestHeader(value = "X-User-Name", defaultValue = "Anonymous") String userName) {

        return reviewService.createReview(productId, userId, userName, request)
                .map(DtoMapper::toReviewDto);
    }

    @GetMapping
    public Flux<ReviewDto> getReviews(@PathVariable String productId) {
        return reviewService.getReviewsByProduct(productId)
                .map(DtoMapper::toReviewDto);
    }

    @GetMapping("/rating")
    public Mono<Map<String, Object>> getRating(@PathVariable String productId) {
        return reviewService.getProductRating(productId);
    }

    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteReview(
            @PathVariable String productId,
            @PathVariable String reviewId,
            @RequestHeader(value = "X-User-Id", defaultValue = "anonymous") String userId) {

        return reviewService.deleteReview(reviewId, userId);
    }
}

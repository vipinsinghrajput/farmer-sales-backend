package com.farmerapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.farmerapp.payload.ReviewRequest;
import com.farmerapp.payload.UpdateReviewRequest;
import com.farmerapp.response.ApiResponse;
import com.farmerapp.response.ReviewResponse;
import com.farmerapp.service.ProductReviewService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/reviews")
@Validated
public class ReviewController {

    @Autowired
    private ProductReviewService reviewService;

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse> submitReview(
    		@NotNull(message = "Consumer ID cannot be null")  @Positive(message = "ID must be positive")@RequestParam Long consumerId,
    		@Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.addReview(consumerId, request));
    }

    @GetMapping("/get")
    public ResponseEntity<List<ReviewResponse>> getReviews(@NotNull(message = "Product ID cannot be null") @RequestParam @Positive(message = "ID must be positive") Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
    }
    
    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateReview(
    		@NotNull(message = "Review ID cannot be null")  @Positive(message = "ID must be positive")@RequestParam  Long reviewId,
    		@NotNull(message = "Consumer ID cannot be null")  @Positive(message = "ID must be positive")@RequestParam  Long consumerId,
           @Valid  @RequestBody UpdateReviewRequest request) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, consumerId, request));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> deleteReview(
    		@NotNull(message = "Review ID cannot be null")  @Positive(message = "ID must be positive")@RequestParam Long reviewId,
    		@NotNull(message = "Consumer ID cannot be null")  @Positive(message = "ID must be positive")@RequestParam  Long consumerId) {
        return ResponseEntity.ok(reviewService.deleteReview(reviewId, consumerId));
    }
}

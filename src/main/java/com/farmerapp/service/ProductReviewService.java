package com.farmerapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.farmerapp.entity.Consumer;
import com.farmerapp.entity.Product;
import com.farmerapp.entity.ProductReview;
import com.farmerapp.exception.ProductNotFoundException;
import com.farmerapp.exception.UserNotFoundException;
import com.farmerapp.payload.ReviewRequest;
import com.farmerapp.payload.UpdateReviewRequest;
import com.farmerapp.repository.ConsumerRepository;
import com.farmerapp.repository.ProductRepository;
import com.farmerapp.repository.ProductReviewRepository;
import com.farmerapp.response.ApiResponse;
import com.farmerapp.response.ReviewResponse;

import jakarta.transaction.Transactional;

@Service
public class ProductReviewService {

    @Autowired
    private ProductRepository productRepo;
    @Autowired 
    private ConsumerRepository consumerRepo;
    @Autowired 
    private ProductReviewRepository reviewRepo;

    public ApiResponse addReview(Long consumerId, ReviewRequest request) {
        Product product = productRepo.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
       
        Consumer consumer = consumerRepo.findById(consumerId)
                .orElseThrow(() -> new UserNotFoundException("Consumer not found"));

        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new ProductNotFoundException("Rating must be between 1 and 5");
        }

        ProductReview review = new ProductReview();
        review.setProduct(product);
        review.setConsumer(consumer);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setReviewDate(LocalDateTime.now());

        reviewRepo.save(review);

        return ApiResponse.builder()
                .message("Product review submitted successfully")
                .build();
    }

    public List<ReviewResponse> getReviewsByProduct(Long productId) {
        return reviewRepo.findByProductId(productId).stream()
                .map(r -> ReviewResponse.builder()
                        .reviewerName(r.getConsumer().getName())
                        .rating(r.getRating())
                        .comment(r.getComment())
                        .reviewDate(r.getReviewDate())
                        .build())
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ApiResponse updateReview(Long reviewId, Long consumerId, UpdateReviewRequest request) {
      
    	ProductReview review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new ProductNotFoundException("Review not found"));

        if (!review.getConsumer().getId().equals(consumerId)) {
            throw new ProductNotFoundException("You can only update your own reviews");
        }

        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new ProductNotFoundException("Rating must be between 1 and 5");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setReviewDate(LocalDateTime.now());

        reviewRepo.save(review);

        return ApiResponse.builder()
                .message("Product review updated successfully")
                .build();
    }
    
    @Transactional
    public ApiResponse deleteReview(Long reviewId, Long consumerId) {
       
    	ProductReview review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new ProductNotFoundException("Review not found"));

        if (!review.getConsumer().getId().equals(consumerId)) {
            throw new ProductNotFoundException("You can only delete your own reviews");
        }

        reviewRepo.delete(review);

        return ApiResponse.builder()
                .message("Product review deleted successfully")
                .build();
    }

}

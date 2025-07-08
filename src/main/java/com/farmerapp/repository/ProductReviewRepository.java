package com.farmerapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.farmerapp.entity.ProductReview;


public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    List<ProductReview> findByProductId(Long productId);
}



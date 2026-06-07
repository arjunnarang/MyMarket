package com.Arjun.MyMarket.product.controller;

import com.Arjun.MyMarket.product.dto.ReviewDto;
import com.Arjun.MyMarket.product.service.impl.ReviewServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewServiceImpl reviewService;

    public ReviewController(ReviewServiceImpl reviewService){
        this.reviewService = reviewService;
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDto> getReviewById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.FOUND).body(reviewService.getReviewById(id));
    }

    @PostMapping("/product/{productId}")
    public ResponseEntity<ReviewDto> createReview(@PathVariable UUID productId, @Valid @RequestBody ReviewDto reviewDto){
        return ResponseEntity.ok(reviewService.createReview(productId, reviewDto));
    }
}

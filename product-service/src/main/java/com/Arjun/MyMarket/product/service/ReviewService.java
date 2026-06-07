package com.Arjun.MyMarket.product.service;

import com.Arjun.MyMarket.product.dto.ReviewDto;

import java.util.UUID;

public interface ReviewService {

    ReviewDto getReviewById(Long id);

    ReviewDto createReview(UUID id, ReviewDto dto);
}

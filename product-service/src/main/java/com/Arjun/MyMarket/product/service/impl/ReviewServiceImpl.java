package com.Arjun.MyMarket.product.service.impl;

import com.Arjun.MyMarket.product.dto.ProductDto;
import com.Arjun.MyMarket.product.dto.ReviewDto;
import com.Arjun.MyMarket.product.entity.Product;
import com.Arjun.MyMarket.product.entity.Review;
import com.Arjun.MyMarket.product.exception.ResourceNotFoundException;
import com.Arjun.MyMarket.product.repository.ProductRepo;
import com.Arjun.MyMarket.product.repository.ReviewRepo;
import com.Arjun.MyMarket.product.service.ReviewService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ProductRepo productRepo;
    private final ReviewRepo reviewRepo;

    public ReviewServiceImpl(ReviewRepo reviewRepo, ProductRepo productRepo){
        this.reviewRepo = reviewRepo;
        this.productRepo = productRepo;
    }


    @Override
    public ReviewDto getReviewById(Long id){
        Review review = reviewRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        return toReviewEntity(review);
    }

    @Override
    public ReviewDto createReview(UUID id, ReviewDto reviewDto){
        Product product = findProduct(id);

        Review review = new Review();

        review.setTitle(reviewDto.getTitle());
        review.setComment(reviewDto.getComment());
        review.setRating(reviewDto.getRating());
        review.setProduct(product);

        return toReviewDto(reviewRepo.save(review));

    }

    @Override
    public ReviewDto updateReview(Long reviewId, ReviewDto reviewDto){
        Review review = findReview(reviewId);
        review.setTitle(reviewDto.getTitle());
        review.setComment(reviewDto.getComment());
        review.setRating(reviewDto.getRating());
        return toReviewDto(reviewRepo.save(review));

    }

    @Override
    public void deleteReview(Long reviewId){
        Review review = findReview(reviewId);

        review.setProduct(null);

        reviewRepo.deleteById(reviewId);
    }

    private Review findReview(Long id){
        return reviewRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Review not found"));
    }

    private ReviewDto toReviewDto(Review review){
        ReviewDto dto = new ReviewDto();

        dto.setId(review.getId());
        dto.setTitle(review.getTitle());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setProductDto(toProductDto(review.getProduct()));

        return dto;
    }
    private Product findProduct(UUID id){
        return productRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product Not Found"));
    }
    private ReviewDto toReviewEntity(Review review){
        ReviewDto reviewDto = new ReviewDto();

        reviewDto.setId(review.getId());
        reviewDto.setTitle(review.getTitle());
        reviewDto.setComment(review.getComment());
        reviewDto.setRating(review.getRating());
        reviewDto.setCreatedAt(review.getCreatedAt());
        if(review.getProduct() != null){
            reviewDto.setProductDto(toProductDto(review.getProduct()));
        }

        return reviewDto;
    }

    private ProductDto toProductDto(Product product){
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setTitle(product.getTitle());
        dto.setShortDesc(product.getShortDesc());
        dto.setLongDesc(product.getLongDesc());
        dto.setPrice(product.getPrice());
        dto.setLive(product.getLive());
        dto.setProductImages(product.getProductImages() == null ? new ArrayList<>() : new ArrayList<>(product.getProductImages()));
        dto.setCategories(new ArrayList<>());
        dto.setReviews(new ArrayList<>());
        dto.setCreatedAt(product.getCreatedAt());
        return dto;
    }
}

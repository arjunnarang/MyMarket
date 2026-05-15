package com.Arjun.MyMarket.product.repository;

import com.Arjun.MyMarket.product.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepo extends JpaRepository<Review, Long> {
}

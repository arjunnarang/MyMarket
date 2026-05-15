package com.Arjun.MyMarket.product.repository;

import com.Arjun.MyMarket.product.entity.Product;
import com.Arjun.MyMarket.product.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewRepo extends JpaRepository<Review, Long> {

    List<Review> findByProduct(Product product);

    List<Review> findByProductId(UUID productId);
}

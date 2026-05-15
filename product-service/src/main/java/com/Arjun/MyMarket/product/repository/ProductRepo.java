package com.Arjun.MyMarket.product.repository;

import com.Arjun.MyMarket.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductRepo extends JpaRepository<Product, UUID> {

    @Query("SELECT p FROM Product p JOIN p.categories c where c.id = :categoryId")
    List<Product> findByCategoryId(@Param("categoryId")String categoryId);

    Page<Product> findByCategory_Id(Long categoryId, Pageable pageable);
}

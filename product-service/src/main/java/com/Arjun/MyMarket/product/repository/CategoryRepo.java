package com.Arjun.MyMarket.product.repository;

import com.Arjun.MyMarket.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepo extends JpaRepository<Category, Long> {

    @Query("select c from Category c join c.products p where p.id = :productId")
    List<Category> findByProductId(@Param("productId") String productId);

    //to find category by category title
    Category findByTitle(String title);
}

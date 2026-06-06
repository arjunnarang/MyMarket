package com.Arjun.MyMarket.product.repository;

import com.Arjun.MyMarket.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CategoryRepo extends JpaRepository<Category, Long> {

    @Query("SELECT c from Category c JOIN FETCH c.products p WHERE p.id = :id")
  List<Category> findCategoriesByProductId(UUID id);

    Category findByTitle(String title);
}

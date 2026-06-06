package com.Arjun.MyMarket.product.service;

import com.Arjun.MyMarket.product.dto.CategoryDto;
import com.Arjun.MyMarket.product.entity.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    List<CategoryDto> getAllCategories();

    CategoryDto getCategoryById(Long id);

    List<CategoryDto> getCategoriesByProductId(UUID id);

    CategoryDto createCategory(CategoryDto categoryDto);
}

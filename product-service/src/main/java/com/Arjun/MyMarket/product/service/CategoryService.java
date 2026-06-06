package com.Arjun.MyMarket.product.service;

import com.Arjun.MyMarket.product.dto.CategoryDto;
import com.Arjun.MyMarket.product.entity.Category;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> getAllCategories();
}

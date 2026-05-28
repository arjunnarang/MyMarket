package com.Arjun.MyMarket.product.service.impl;

import com.Arjun.MyMarket.product.dto.CategoryDto;
import com.Arjun.MyMarket.product.entity.Category;
import com.Arjun.MyMarket.product.repository.CategoryRepo;
import com.Arjun.MyMarket.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepo categoryRepo;


    private void toCategoryEntity(CategoryDto categoryDto, Category category){
        category.setTitle(categoryDto.getTitle());
        category.setProducts(new ArrayList<>());
    }

    private CategoryDto toCategoryDto(Category category){
        CategoryDto categoryDto = new CategoryDto();

        categoryDto.setTitle(category.getTitle());
        categoryDto.setId(category.getId());
        categoryDto.setProductDtos(new ArrayList<>());

        return categoryDto;
    }
}

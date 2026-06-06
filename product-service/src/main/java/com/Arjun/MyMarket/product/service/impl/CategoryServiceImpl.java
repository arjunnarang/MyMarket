package com.Arjun.MyMarket.product.service.impl;

import com.Arjun.MyMarket.product.dto.CategoryDto;
import com.Arjun.MyMarket.product.dto.ProductDto;
import com.Arjun.MyMarket.product.entity.Category;
import com.Arjun.MyMarket.product.repository.CategoryRepo;
import com.Arjun.MyMarket.product.service.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    private final CategoryRepo categoryRepo;

    public CategoryServiceImpl(CategoryRepo categoryRepo){
        this.categoryRepo = categoryRepo;
    }

    @Override
    @Transactional
    public List<CategoryDto> getAllCategories(){
        List <Category> categoryEntity = categoryRepo.findAll();
        return categoryEntity.stream().map(this::toCategoryDto).collect(Collectors.toList());
    }


    private CategoryDto toCategoryDto(Category category){
        CategoryDto dto = new CategoryDto();

        dto.setId((category.getId()));
        dto.setTitle(category.getTitle());

        dto.setProductDtos(category.getProducts() == null ? new ArrayList<>() : category.getProducts().stream().map(product -> {
            var productDto = new ProductDto();
            productDto.setId(product.getId());
            productDto.setTitle(product.getTitle());
            productDto.setShortDesc(product.getShortDesc());
            productDto.setLongDesc(product.getLongDesc());
            productDto.setPrice(product.getPrice());
            productDto.setLive(product.getLive());
            logger.debug("The code is coming here");
            productDto.setProductImages(product.getProductImages() == null ? new ArrayList<>() : new ArrayList<>(product.getProductImages()));
            productDto.setCategories(new ArrayList<>());
            productDto.setReviews(new ArrayList<>());

            return productDto;
        }).collect(Collectors.toList()));

        return dto;
    }
}

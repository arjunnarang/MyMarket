package com.Arjun.MyMarket.product.controller;

import com.Arjun.MyMarket.product.dto.CategoryDto;
import com.Arjun.MyMarket.product.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories(){
        return new ResponseEntity <>(categoryService.getAllCategories(), HttpStatus.OK);
    }
}

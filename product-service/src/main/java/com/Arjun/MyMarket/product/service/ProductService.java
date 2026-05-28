package com.Arjun.MyMarket.product.service;

import com.Arjun.MyMarket.product.dto.ProductDto;
import com.Arjun.MyMarket.product.dto.ReviewDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    //Create Product
    ProductDto createProduct(ProductDto productDto);

    //Update Product
    ProductDto updateProduct(UUID id, ProductDto productDto);

    //find product by id
    ProductDto getProductById(UUID id);

    //Delete product
    void deleteProduct(UUID productId);

    //Add category to product
    ProductDto addCategoryToProduct(UUID productId, Long categoryId);

    //Add review to product
    ReviewDto addReviewToProduct(UUID productId, ReviewDto reviewDto);

    //Add product images
    ProductDto addProductImages(UUID productId, List<MultipartFile> images);

    //get product images
    List<String> getProductImages(UUID productId);

    List<ProductDto> getAllProducts();

    List<ProductDto> getProductsByCategoryId(Long categoryId);
}

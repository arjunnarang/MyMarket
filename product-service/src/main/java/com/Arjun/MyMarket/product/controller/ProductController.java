package com.Arjun.MyMarket.product.controller;

import com.Arjun.MyMarket.product.dto.ProductDto;
import com.Arjun.MyMarket.product.dto.ReviewDto;
import com.Arjun.MyMarket.product.service.ProductService;
import com.Arjun.MyMarket.product.service.impl.ProductServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    //getting product by Id
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable UUID productId){
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    //creating product
    @PostMapping
    public ResponseEntity<ProductDto> createNewProduct(@RequestBody ProductDto productDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productDto));
    }

    //delete product by id
    @DeleteMapping("/{productId}")
    public void deleteById(@PathVariable UUID productId){
        productService.deleteProduct(productId);
    }

    //listing all products
    @GetMapping("/all")
    public ResponseEntity<List<ProductDto>> getAllProducts(){
        List<ProductDto> productDtoList = productService.getAllProducts();
        return ResponseEntity.ok(productDtoList);
    }

    //Updating product by id
    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable UUID productId, @RequestBody ProductDto productDto){
        return ResponseEntity.ok(productService.updateProduct(productId, productDto));
    }

    //Getting products present inside a category using category id
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDto>> getProductsByCategoryId(@PathVariable Long categoryId){
        return ResponseEntity.ok(productService.getProductsByCategoryId(categoryId));
    }

    //Adding category to product
    @PostMapping("/{productId}/category/{categoryId}")
    public ResponseEntity<ProductDto> addCategoryToProduct(@PathVariable UUID productId, @PathVariable Long categoryId){
        return ResponseEntity.ok(productService.addCategoryToProduct(productId, categoryId));
    }

    //removing category from a product
    @DeleteMapping("/{productId}/category/{categoryId}")
    public ResponseEntity<ProductDto> removeCategoryFromProduct(@PathVariable UUID productId, @PathVariable Long categoryId){
        return ResponseEntity.ok(productService.removeCategoryFromProduct(productId, categoryId));
    }

    // add review to product
    @PostMapping("/{productId}/reviews")
    public ResponseEntity<ReviewDto> addReviewToProduct(@PathVariable UUID productId, @RequestBody ReviewDto reviewDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addReviewToProduct(productId, reviewDto));
    }

    //add images to product
    @PostMapping(value="/{productId}/images", consumes="multipart/form-data")
    public ResponseEntity<ProductDto> addImagesToProduct(@PathVariable UUID productId, @RequestParam("files") List<MultipartFile> files){
        return ResponseEntity.ok(productService.addProductImages(productId, files));
    }
}

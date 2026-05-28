package com.Arjun.MyMarket.product.controller;

import com.Arjun.MyMarket.product.dto.ProductDto;
import com.Arjun.MyMarket.product.service.ProductService;
import com.Arjun.MyMarket.product.service.impl.ProductServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable UUID productId){
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @PostMapping
    public ResponseEntity<ProductDto> createNewProduct(@RequestBody ProductDto productDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productDto));
    }

    @DeleteMapping("/{productId}")
    public void deleteById(@PathVariable UUID productId){
        productService.deleteProduct(productId);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductDto>> getAllProducts(){
        List<ProductDto> productDtoList = productService.getAllProducts();
        return ResponseEntity.ok(productDtoList);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable UUID productId, @RequestBody ProductDto productDto){
        return ResponseEntity.ok(productService.updateProduct(productId, productDto));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDto>> getProductsByCategoryId(@PathVariable Long categoryId){
        return ResponseEntity.ok(productService.getProductsByCategoryId(categoryId));
    }
}

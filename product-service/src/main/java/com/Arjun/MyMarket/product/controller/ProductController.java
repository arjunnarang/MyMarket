package com.Arjun.MyMarket.product.controller;

import com.Arjun.MyMarket.product.dto.ProductDto;
import com.Arjun.MyMarket.product.service.ProductService;
import com.Arjun.MyMarket.product.service.impl.ProductServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


}

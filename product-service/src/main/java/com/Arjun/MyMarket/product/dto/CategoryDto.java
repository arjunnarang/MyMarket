package com.Arjun.MyMarket.product.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;
    private List<ProductDto> productDtos;
}

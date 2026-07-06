package com.Arjun.MyMarket.product.dto;

import com.Arjun.MyMarket.product.entity.Category;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

    private UUID id;
    private String title;
    private String shortDesc;
    private String longDesc;
    private Double price;
    private Boolean live=false;
    private List<String> productImages;
    private List<CategoryDto> categories;
    private List<ReviewDto> reviews;
    private Instant createdAt;
    private Instant updatedAt;
}

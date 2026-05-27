package com.Arjun.MyMarket.product.dto;

import com.Arjun.MyMarket.product.entity.Product;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto {


    private Long id;
    private String title;
    private String comment;
    private Integer rating;
    private ProductDto productDto;

}

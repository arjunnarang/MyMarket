package com.Arjun.MyMarket.product.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue
    private UUID id;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String shortDesc;

    @Lob
    private String longDesc;
    private Double price;
    private Boolean live=false;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> productImages = new ArrayList<>();

    @ManyToMany(mappedBy = "products")
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @CreatedDate
    private Instant createdAt;
}

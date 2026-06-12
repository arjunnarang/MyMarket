package com.Arjun.MyMarket.product.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@EntityListeners(AuditingEntityListener.class)
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

    //@CreationTimestamp //this annotation is for hibernate time and it works normally to store time stamp
    @CreatedDate  //this annotation is for JPA auditing and it is used with @EnableJpaAuditing in main class and also @EntityListeners in this class
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}

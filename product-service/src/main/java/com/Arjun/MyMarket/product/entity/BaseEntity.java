package com.Arjun.MyMarket.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

//Declares a class which is not itself an entity, but whose mappings are inherited by the entities which extend it.
@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
//all other entities extends BaseEntity class due to which all entities can inherit this classes' variables
public abstract class BaseEntity {

    //@CreationTimestamp //this annotation is for hibernate time and it works normally to store time stamp
    @CreatedDate
    //this annotation is for JPA auditing and it is used with @EnableJpaAuditing in main class and also @EntityListeners in this class
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;
}

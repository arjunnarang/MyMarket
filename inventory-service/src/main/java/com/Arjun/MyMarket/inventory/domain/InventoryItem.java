package com.Arjun.MyMarket.inventory.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "inventories", uniqueConstraints = {
        @UniqueConstraint(name = "uk_inventory_sku", columnNames = "sku"),
        @UniqueConstraint(name = "uk_inventory_product_id", columnNames = "productId")
})
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private UUID productId;

    @Column(nullable = false, unique = true, length = 128)
    private String sku;

    @Column(nullable = false, updatable = false)
    private String productName;

    @Column(nullable = false, length = 120)
    private String warehouseLocation;

    @Column(nullable = false)
    private Integer availableQuantity;

    @Column(nullable = false)
    private Integer reservedQuantity;

    @Column(nullable = false)
    private Integer reorderLevel;

    @Column(nullable = false)
    private Boolean active;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    private String reasonToAdjust;

    @PrePersist
    void onCreate(){
        Instant now = Instant.now();

        if(createdAt == null){
            createdAt = now;
        }

        updatedAt = now;

        if(availableQuantity == null){
            availableQuantity = 0;
        }

        if(reservedQuantity == null){
            reservedQuantity = 0;
        }

        if(reorderLevel == null){
            reorderLevel = null;
        }
    }

    @PreUpdate
    void update(){
        Instant now = Instant.now();

        updatedAt = now;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getWarehouseLocation() {
        return warehouseLocation;
    }

    public void setWarehouseLocation(String warehouseLocation) {
        this.warehouseLocation = warehouseLocation;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Integer getReservedQuantity() {
        return reservedQuantity;
    }

    public void setReservedQuantity(Integer reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }

    public Integer getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getReasonToAdjust() {
        return reasonToAdjust;
    }

    public void setReasonToAdjust(String reasonToAdjust) {
        this.reasonToAdjust = reasonToAdjust;
    }
}

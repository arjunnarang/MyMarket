package com.Arjun.MyMarket.inventory.repository;

import com.Arjun.MyMarket.inventory.domain.InventoryItem;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    Optional<InventoryItem> findBySku(String sku);

    Optional<InventoryItem> findByProductId(UUID productId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from InventoryItem i where i.id = :id")
    Optional<InventoryItem> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from InventoryItem i where i.productId = :productId")
    Optional<InventoryItem> findByProductIdForUpdate(@Param("id")UUID productId);

    boolean existsBySku(String sku);

    boolean existsByProductId(UUID productId);

    //the name of this method is a kind of naming convention where we tell JPA what kind of query to create
    // for this method. Here
    //findBy ActiveTrue - means product is active
    //OrderByProductNameAsc - means sort the products in ascending order by name
    List<InventoryItem> findByActiveTrueOrderByProductNameAsc();

    
    List<InventoryItem> findByAvailableQuantityLessThanEqualAndActiveTrueOrderByAvailableQuantityAsc(int threshold);
}

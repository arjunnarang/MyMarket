package com.Arjun.MyMarket.inventory.service;


import com.Arjun.MyMarket.inventory.domain.InventoryItem;
import com.Arjun.MyMarket.inventory.dto.InventoryResponse;
import com.Arjun.MyMarket.inventory.exception.ResourceNotFoundException;
import com.Arjun.MyMarket.inventory.repository.InventoryItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService{

    private final InventoryItemRepository repository;

    public InventoryServiceImpl(InventoryItemRepository repository){
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getById(Long id){
        return toResponse(findInventoryEntity(id));
    }

    private InventoryResponse toResponse(InventoryItem item){
        return new InventoryResponse(
                item.getId(),
                item.getProductId(),
                item.getSku(),
                item.getProductName(),
                item.getWarehouseLocation(),
                item.getAvailableQuantity(),
                item.getReservedQuantity(),
                item.getReorderLevel(),
                item.getActive(),
                item.getTotalQuantity(),
                item.isLowStock(),
                item.getReasonToAdjust(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }

    private InventoryItem findInventoryEntity(Long id){
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Inventory not found: " + id));
    }
}

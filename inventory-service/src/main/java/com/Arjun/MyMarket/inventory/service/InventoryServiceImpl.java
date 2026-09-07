package com.Arjun.MyMarket.inventory.service;


import com.Arjun.MyMarket.inventory.domain.InventoryItem;
import com.Arjun.MyMarket.inventory.dto.CreateInventoryRequest;
import com.Arjun.MyMarket.inventory.dto.InventoryResponse;
import com.Arjun.MyMarket.inventory.dto.ProductSnapshot;
import com.Arjun.MyMarket.inventory.dto.UpdateInventoryRequest;
import com.Arjun.MyMarket.inventory.exception.BusinessRuleException;
import com.Arjun.MyMarket.inventory.exception.ResourceNotFoundException;
import com.Arjun.MyMarket.inventory.external.ProductClient;
import com.Arjun.MyMarket.inventory.repository.InventoryItemRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService{

    private final InventoryItemRepository repository;
    private final ProductClient client;
    public InventoryServiceImpl(InventoryItemRepository repository, ProductClient client){
        this.repository = repository;
        this.client = client;
    }


    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getById(Long id){
        return toResponse(findInventoryEntity(id));
    }

    @Override
    public InventoryResponse createInventory(CreateInventoryRequest request) {

        //checking if product  exists  or not
        ProductSnapshot product = null;
        try{
            product = client.getProductById(request.productId());
        }catch (Exception e){
            throw new ResourceNotFoundException("Product not found: " + request.productId());
        }

        //checking by sku is inventory exists
        String sku = normalizeSku(request.sku());

        if(repository.existsBySku(sku)){
            throw new BusinessRuleException("Inventory exists already: " + sku);
        }

        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setProductId(request.productId());
        inventoryItem.setSku(sku);
        inventoryItem.setProductName(product.title());
        inventoryItem.setWarehouseLocation(request.wareHouseLocation());
        inventoryItem.setAvailableQuantity(request.availableQuantity());
        inventoryItem.setReservedQuantity(request.reservedQuantity());
        inventoryItem.setReorderLevel(request.reorderLevel());

        //if request.active == null is true then set 'true' means we are creating the inventory
        //if request.active is false that means inventory is getting created but is disabled
        inventoryItem.setActive(request.active() == null || request.active());

        return toResponse(repository.save(inventoryItem));
    }

    @Override
    public InventoryResponse updateInventory(Long id, UpdateInventoryRequest request){
        InventoryItem inventory = findInventoryEntity(id);

        inventory.setProductName(request.productName());
        inventory.setWarehouseLocation(request.warehouseLocation());
        inventory.setReorderLevel(request.reorderLevel());
        inventory.setActive(request.active());

        return toResponse(repository.save(inventory));
    }

    private String normalizeSku(String sku){
        if(StringUtils.isBlank(sku)){
            throw new BusinessRuleException("Sku is required");
        }

        //for ex. IPHONE-16-PRO
        return sku.trim().toUpperCase();
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

package com.Arjun.MyMarket.inventory.service;


import com.Arjun.MyMarket.inventory.domain.InventoryItem;
import com.Arjun.MyMarket.inventory.dto.*;
import com.Arjun.MyMarket.inventory.exception.BusinessRuleException;
import com.Arjun.MyMarket.inventory.exception.ResourceNotFoundException;
import com.Arjun.MyMarket.inventory.external.ProductClient;
import com.Arjun.MyMarket.inventory.repository.InventoryItemRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getBySku(String sku) {
        return toResponse(repository.findBySku(normalizeSku(sku))
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for sku: " + sku)));
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getByProductId(UUID productId) {
        return toResponse(repository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for productId: " + productId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getLowStock(int threshold) {
        return repository.findByAvailableQuantityLessThanEqualAndActiveTrueOrderByAvailableQuantityAsc(threshold)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public InventoryResponse adjustStock(Long id, AdjustStockRequest request){
        InventoryItem inventoryItem = findEntityForUpdate(id);

        int nextAvailable = safeInt(inventoryItem.getAvailableQuantity()) + request.quantityDelta();

        if(nextAvailable < 0){
            throw new BusinessRuleException("Put valid quantity..");
        }

        inventoryItem.setAvailableQuantity(nextAvailable);
        inventoryItem.setReasonToAdjust(request.reason());
        return toResponse(repository.save(inventoryItem));
    }

    @Override
    public InventoryResponse reserveStock(Long id, ReserveStockRequest request){
        InventoryItem inventoryItem = findEntityForUpdate(id);

        int availableQuantity = inventoryItem.getAvailableQuantity();
        int reserveQuantity = inventoryItem.getReservedQuantity();

        if(availableQuantity < reserveQuantity){
            throw new BusinessRuleException("Low stock");
        }

        inventoryItem.setAvailableQuantity(safeInt(availableQuantity) - reserveQuantity);
        inventoryItem.setReservedQuantity(safeInt(reserveQuantity) + request.quantity());
        return toResponse(repository.save(inventoryItem));
    }

    @Override
    public InventoryResponse releaseStock(Long id, ReleaseStockRequest request) {
        InventoryItem item = findEntityForUpdate(id);
        int quantity = request.quantity();
        int reserved = safeInt(item.getReservedQuantity());
        if (reserved < quantity) {
            throw new BusinessRuleException("Insufficient reserved stock to release");
        }
        item.setReservedQuantity(reserved - quantity);
        item.setAvailableQuantity(safeInt(item.getAvailableQuantity()) + quantity);
        return toResponse(repository.save(item));
    }

    @Override
    public InventoryResponse reserveStockByProductId(UUID productId, ReserveStockRequest request) {
        InventoryItem item = repository.findByProductIdForUpdate(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for productId: " + productId));
        return reserve(item, request.quantity());
    }


    @Override
    public void delete(Long id) {
        InventoryItem item = findInventoryEntity(id);
        repository.delete(item);
    }

    @Override
    public InventoryResponse releaseStockByProductId(UUID productId, ReleaseStockRequest request) {
        InventoryItem item = repository.findByProductIdForUpdate(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for productId: " + productId));
        return release(item, request.quantity());
    }


    private InventoryResponse reserve(InventoryItem item, int quantity) {
        int available = safeInt(item.getAvailableQuantity());
        if (available < quantity) {
            throw new BusinessRuleException("Insufficient available stock to reserve");
        }
        item.setAvailableQuantity(available - quantity);
        item.setReservedQuantity(safeInt(item.getReservedQuantity()) + quantity);
        return toResponse(repository.save(item));
    }

    private InventoryResponse release(InventoryItem item, int quantity) {
        int reserved = safeInt(item.getReservedQuantity());
        if (reserved < quantity) {
            throw new BusinessRuleException("Insufficient reserved stock to release");
        }
        item.setReservedQuantity(reserved - quantity);
        item.setAvailableQuantity(safeInt(item.getAvailableQuantity()) + quantity);
        return toResponse(repository.save(item));
    }

    private InventoryItem findEntityForUpdate(Long id) {
        return repository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for id: " + id));
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

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }
}

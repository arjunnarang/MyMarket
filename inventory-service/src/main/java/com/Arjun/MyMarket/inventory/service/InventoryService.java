package com.Arjun.MyMarket.inventory.service;

import com.Arjun.MyMarket.inventory.dto.*;

import java.util.List;
import java.util.UUID;

public interface InventoryService {

    InventoryResponse createInventory(CreateInventoryRequest request);

    //update productname, warehouselocation, reorderlevel, active
    InventoryResponse updateInventory(Long id, UpdateInventoryRequest request);

    InventoryResponse getById(Long id);

    InventoryResponse getBySku(String sku);

    InventoryResponse getByProductId(UUID productId);

    List<InventoryResponse> getAll();

    List<InventoryResponse> getLowStock(int threshold);

    InventoryResponse adjustStock(Long id, AdjustStockRequest request);

    InventoryResponse reserveStock(Long id, ReserveStockRequest request);

    InventoryResponse releaseStock(Long id, ReleaseStockRequest request);

    InventoryResponse releaseStockByProductId(UUID productId, ReleaseStockRequest request);

    InventoryResponse reserveStockByProductId(UUID productId, ReserveStockRequest request);

    void delete(Long id);
}

package com.Arjun.MyMarket.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateInventoryRequest(
        @NotNull UUID productId,
        @NotBlank String sku,
        String productName,
        @NotBlank String wareHouseLocation,
        @NotNull @Min(0) Integer availableQuantity,
        @Min(0) Integer reservedQuantity,
        @Min(0) Integer reorderLevel,
        Boolean active
) {
}

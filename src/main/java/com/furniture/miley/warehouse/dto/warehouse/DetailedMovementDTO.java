package com.furniture.miley.warehouse.dto.warehouse;
import com.furniture.miley.catalog.dto.product.ProductDTO;
import com.furniture.miley.purchase.dto.rawMaterial.RawMaterialDTO;
import com.furniture.miley.warehouse.dto.grocer.GrocerDTO;
import com.furniture.miley.warehouse.enums.InventoryMovementType;
import com.furniture.miley.warehouse.model.InventoryMovements;

import java.sql.Timestamp;

public record DetailedMovementDTO (
        String id,
        InventoryMovementType type,
        GrocerDTO grocer,
        Integer initialStock,
        Integer amount,
        Integer newStock,
        Timestamp date,
        String reason,
        RawMaterialDTO rawMaterial,
        ProductDTO productDTO,
        String warehouse,
        String guide
){
    public static DetailedMovementDTO toDTO(InventoryMovements inventoryMovements){
        return new DetailedMovementDTO(
                inventoryMovements.getId(),
                inventoryMovements.getType(),
                inventoryMovements.getExitGuide() != null ? GrocerDTO.toDTO(inventoryMovements.getExitGuide().getGrocer()) : GrocerDTO.toDTO(inventoryMovements.getEntryGuide().getGrocer()),
                inventoryMovements.getInitialStock(),
                inventoryMovements.getAmount(),
                inventoryMovements.getNewStock(),
                inventoryMovements.getDate(),
                inventoryMovements.getReason(),
                inventoryMovements.getRawMaterial() != null ? RawMaterialDTO.toDTO(inventoryMovements.getRawMaterial()) : null,
                inventoryMovements.getProduct() != null ? ProductDTO.toDTO(inventoryMovements.getProduct()) : null,
                inventoryMovements.getWarehouse().getLocation(),
                inventoryMovements.getEntryGuide() != null ? inventoryMovements.getEntryGuide().getId() : inventoryMovements.getExitGuide().getId()
        );
    }
}

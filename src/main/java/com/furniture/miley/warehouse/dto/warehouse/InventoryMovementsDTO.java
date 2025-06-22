package com.furniture.miley.warehouse.dto.warehouse;

import com.furniture.miley.warehouse.enums.InventoryMovementType;
import com.furniture.miley.warehouse.model.InventoryMovements;

import java.sql.Timestamp;

public record InventoryMovementsDTO(
        String id,
        InventoryMovementType type,
        String grocer,
        Integer initialStock,
        Integer amount,
        Integer newStock,
        Timestamp date,
        String reason,
        String productOrMaterial,
        String warehouse
) {
    public static InventoryMovementsDTO toDTO(InventoryMovements inventoryMovements){
        return new InventoryMovementsDTO(
                inventoryMovements.getId(),
                inventoryMovements.getType(),
                inventoryMovements.getEntryGuide() != null ? inventoryMovements.getEntryGuide().getGrocer().getUser().getPersonalInformation().getFullName() : inventoryMovements.getExitGuide().getGrocer().getUser().getPersonalInformation().getFullName(),
                inventoryMovements.getInitialStock(),
                inventoryMovements.getAmount(),
                inventoryMovements.getNewStock(),
                inventoryMovements.getDate(),
                inventoryMovements.getReason(),
                inventoryMovements.getRawMaterial() != null ? inventoryMovements.getRawMaterial().getName() : inventoryMovements.getProduct().getName(),
                inventoryMovements.getWarehouse() != null ? inventoryMovements.getWarehouse().getLocation() : "No aplica"
        );
    }
}

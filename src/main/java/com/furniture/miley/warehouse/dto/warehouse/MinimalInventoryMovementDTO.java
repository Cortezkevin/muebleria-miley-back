package com.furniture.miley.warehouse.dto.warehouse;

import com.furniture.miley.purchase.enums.MeasurementUnit;
import com.furniture.miley.warehouse.model.InventoryMovements;

import java.sql.Timestamp;

public record MinimalInventoryMovementDTO(
        String id,
        MeasurementUnit unitType,
        Integer amount,
        Timestamp date,
        String productOrMaterial
) {
    public static MinimalInventoryMovementDTO parseToDTO(InventoryMovements inventoryMovements){
        return new MinimalInventoryMovementDTO(
                inventoryMovements.getId(),
                inventoryMovements.getProduct() != null ? MeasurementUnit.CANTIDAD : inventoryMovements.getRawMaterial().getMeasurementUnit(),
                inventoryMovements.getAmount(),
                inventoryMovements.getDate(),
                inventoryMovements.getRawMaterial() != null ? inventoryMovements.getRawMaterial().getName() : inventoryMovements.getProduct().getName()
        );
    }
}

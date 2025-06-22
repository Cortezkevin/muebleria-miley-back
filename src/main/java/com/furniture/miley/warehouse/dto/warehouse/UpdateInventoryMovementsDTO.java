package com.furniture.miley.warehouse.dto.warehouse;
import com.furniture.miley.warehouse.enums.InventoryMovementType;

public record UpdateInventoryMovementsDTO(
        String id,
        InventoryMovementType type,
        Integer amount,
        String reason,
        String productOrMaterialId,
        String warehouse
) {
}

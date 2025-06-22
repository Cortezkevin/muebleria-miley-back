package com.furniture.miley.warehouse.dto.warehouse;

import com.furniture.miley.warehouse.enums.InventoryMovementType;

import java.util.List;

public record CreateInventoryMovementDTO(
        InventoryMovementType type,
        String reason,
        String conditions,
        List<MaterialOrProductDTO> materialOrProducts,
        String grocerId,
        String warehouse
) {
}

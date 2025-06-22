package com.furniture.miley.warehouse.dto.warehouse;

import com.furniture.miley.warehouse.model.Warehouse;

public record WarehouseDTO(
        String id,
        String location
) {
    public static WarehouseDTO toDTO(Warehouse warehouse){
        return new WarehouseDTO(
                warehouse.getId(),
                warehouse.getLocation()
        );
    }
}

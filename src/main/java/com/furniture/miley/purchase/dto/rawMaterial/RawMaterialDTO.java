package com.furniture.miley.purchase.dto.rawMaterial;

import com.furniture.miley.purchase.enums.MeasurementUnit;
import com.furniture.miley.purchase.model.RawMaterial;

import java.math.BigDecimal;

public record RawMaterialDTO(
        String id,
        String name,
        String description,
        MeasurementUnit measurementUnit,
        Integer stock,
        BigDecimal unitPrice,
        String supplier,
        String supplierId
) {
    public static RawMaterialDTO toDTO(RawMaterial rawMaterial){
        return new RawMaterialDTO(
                rawMaterial.getId(),
                rawMaterial.getName(),
                rawMaterial.getDescription(),
                rawMaterial.getMeasurementUnit(),
                rawMaterial.getStock(),
                rawMaterial.getUnitPrice(),
                rawMaterial.getSupplier().getName(),
                rawMaterial.getSupplier().getId()
        );
    }
}

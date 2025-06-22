package com.furniture.miley.purchase.dto.rawMaterial;

import com.furniture.miley.purchase.enums.MeasurementUnit;

import java.math.BigDecimal;

public record CreateRawMaterialDTO(
        String name,
        String description,
        BigDecimal unitPrice,
        MeasurementUnit measurementUnit,
        String supplierId
) {
}

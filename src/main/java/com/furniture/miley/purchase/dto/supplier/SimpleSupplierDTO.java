package com.furniture.miley.purchase.dto.supplier;

import com.furniture.miley.purchase.model.Supplier;

public record SimpleSupplierDTO(
        String id,
        String name
) {

    public static SimpleSupplierDTO toDTO(Supplier supplier) {
        return new SimpleSupplierDTO(
                supplier.getId(),
                supplier.getName()
        );
    }
}

package com.furniture.miley.purchase.dto.supplier;

import com.furniture.miley.purchase.model.Supplier;

public record SupplierDTO (
        String id,
        String name,
        String ruc,
        String phone,
        String address
){
    public static SupplierDTO toDTO(Supplier supplier){
        return new SupplierDTO(
                supplier.getId(),
                supplier.getName(),
                supplier.getRuc(),
                supplier.getPhone(),
                supplier.getAddress()
        );
    }
}

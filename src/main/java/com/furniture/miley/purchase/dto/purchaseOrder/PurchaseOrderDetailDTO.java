package com.furniture.miley.purchase.dto.purchaseOrder;

import com.furniture.miley.purchase.enums.MeasurementUnit;
import com.furniture.miley.purchase.enums.PurchaseOrderDetailStatus;
import com.furniture.miley.purchase.model.PurchaseOrderDetail;

import java.math.BigDecimal;

public record PurchaseOrderDetailDTO (
        String id,
        String name,
        Integer amount,
        MeasurementUnit measurementUnit,
        PurchaseOrderDetailStatus status,
        BigDecimal unitPrice,
        BigDecimal total
){
    public static PurchaseOrderDetailDTO toDTO(PurchaseOrderDetail purchaseOrderDetail){
        return new PurchaseOrderDetailDTO(
                purchaseOrderDetail.getId(),
                purchaseOrderDetail.getProduct() != null ? purchaseOrderDetail.getProduct().getName() : purchaseOrderDetail.getRawMaterial().getName(),
                purchaseOrderDetail.getAmount(),
                purchaseOrderDetail.getProduct() != null ? MeasurementUnit.CANTIDAD : purchaseOrderDetail.getRawMaterial().getMeasurementUnit(),
                purchaseOrderDetail.getStatus(),
                purchaseOrderDetail.getUnitPrice(),
                purchaseOrderDetail.getTotal()
        );
    }
}

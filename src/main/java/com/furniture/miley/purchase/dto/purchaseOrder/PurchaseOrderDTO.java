package com.furniture.miley.purchase.dto.purchaseOrder;

import com.furniture.miley.purchase.enums.PurchaseOrderStatus;
import com.furniture.miley.purchase.model.PurchaseOrder;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record PurchaseOrderDTO(
        String id,
        Timestamp date,
        PurchaseOrderStatus status,
        BigDecimal total,
        String requester,
        String supplier,
        String purchaseOrderReceptionId,
        String supplierId,
        String userId,
        String guide
) {
    public static PurchaseOrderDTO toDTO(PurchaseOrder purchaseOrder){
        return new PurchaseOrderDTO(
                purchaseOrder.getId(),
                purchaseOrder.getDate(),
                purchaseOrder.getStatus(),
                purchaseOrder.getTotal(),
                purchaseOrder.getUser().getPersonalInformation().getFullName(),
                purchaseOrder.getSupplier().getName(),
                purchaseOrder.getPurchaseOrderReception().getId(),
                purchaseOrder.getSupplier().getId(),
                purchaseOrder.getUser().getId(),
                purchaseOrder.getEntryGuide() != null ? purchaseOrder.getEntryGuide().getId() : purchaseOrder.getRejectionGuide() != null ? purchaseOrder.getRejectionGuide().getId() : null
        );
    }
}

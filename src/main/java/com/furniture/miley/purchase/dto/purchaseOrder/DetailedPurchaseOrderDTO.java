package com.furniture.miley.purchase.dto.purchaseOrder;

import com.furniture.miley.purchase.dto.supplier.SupplierDTO;
import com.furniture.miley.purchase.enums.PurchaseOrderStatus;
import com.furniture.miley.purchase.model.PurchaseOrder;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public record DetailedPurchaseOrderDTO(
        String id,
        Timestamp date,
        PurchaseOrderStatus status,
        BigDecimal total,
        String requester,
        SupplierDTO supplier,
        List<PurchaseOrderDetailDTO> orderDetails,
        String userId,
        String guide
) {
    public static DetailedPurchaseOrderDTO toDTO(PurchaseOrder purchaseOrder){
        return new DetailedPurchaseOrderDTO(
                purchaseOrder.getId(),
                purchaseOrder.getDate(),
                purchaseOrder.getStatus(),
                purchaseOrder.getTotal(),
                purchaseOrder.getUser().getPersonalInformation().getFullName(),
                SupplierDTO.toDTO( purchaseOrder.getSupplier() ),
                purchaseOrder.getPurchaseOrderDetails().stream().map( PurchaseOrderDetailDTO::toDTO ).toList(),
                purchaseOrder.getUser().getId(),
                purchaseOrder.getEntryGuide() != null ? purchaseOrder.getEntryGuide().getId() : purchaseOrder.getRejectionGuide() != null ? purchaseOrder.getRejectionGuide().getId() : null
        );
    }
}

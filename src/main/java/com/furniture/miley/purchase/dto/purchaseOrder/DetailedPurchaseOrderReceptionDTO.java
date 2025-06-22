package com.furniture.miley.purchase.dto.purchaseOrder;

import com.furniture.miley.purchase.dto.supplier.SupplierDTO;
import com.furniture.miley.purchase.enums.PurchaseOrderReceptionStatus;
import com.furniture.miley.purchase.model.PurchaseOrderReception;
import com.furniture.miley.warehouse.dto.grocer.GrocerDTO;

import java.sql.Timestamp;
import java.util.List;

public record DetailedPurchaseOrderReceptionDTO(
        String id,
        String observations,
        Timestamp createdDate,
        Timestamp startDate,
        Timestamp reviewDate,
        Timestamp completedDate,
        PurchaseOrderReceptionStatus status,
        SupplierDTO supplier,
        GrocerDTO grocer,
        List<PurchaseOrderDetailDTO> purchaseOrderDetails
) {
    public static DetailedPurchaseOrderReceptionDTO toDTO(PurchaseOrderReception purchaseOrderReception){
        return new DetailedPurchaseOrderReceptionDTO(
                purchaseOrderReception.getId(),
                purchaseOrderReception.getObservations(),
                purchaseOrderReception.getCreatedDate(),
                purchaseOrderReception.getStartDate(),
                purchaseOrderReception.getReviewDate(),
                purchaseOrderReception.getCompletedDate(),
                purchaseOrderReception.getStatus(),
                SupplierDTO.toDTO( purchaseOrderReception.getPurchaseOrder().getSupplier() ),
                GrocerDTO.toDTO( purchaseOrderReception.getGrocer() ),
                purchaseOrderReception.getPurchaseOrder().getPurchaseOrderDetails().stream().map(PurchaseOrderDetailDTO::toDTO).toList()
        );
    }
}

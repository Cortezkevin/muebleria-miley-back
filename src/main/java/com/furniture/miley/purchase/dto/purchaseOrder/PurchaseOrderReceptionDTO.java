package com.furniture.miley.purchase.dto.purchaseOrder;

import com.furniture.miley.purchase.enums.PurchaseOrderReceptionStatus;
import com.furniture.miley.purchase.enums.PurchaseOrderStatus;
import com.furniture.miley.purchase.model.PurchaseOrderReception;


import java.sql.Timestamp;

public record PurchaseOrderReceptionDTO(
        String id,
        String observations,
        Timestamp createdDate,
        Timestamp startDate,
        Timestamp reviewDate,
        Timestamp completedDate,
        PurchaseOrderStatus purchaseOrderStatus,
        PurchaseOrderReceptionStatus status,
        String purchaseOrderId,
        String grocer,
        String grocerId
) {
    public static PurchaseOrderReceptionDTO toDTO(PurchaseOrderReception purchaseOrderReception){
        return new PurchaseOrderReceptionDTO(
                purchaseOrderReception.getId(),
                purchaseOrderReception.getObservations(),
                purchaseOrderReception.getCreatedDate(),
                purchaseOrderReception.getStartDate(),
                purchaseOrderReception.getReviewDate(),
                purchaseOrderReception.getCompletedDate(),
                purchaseOrderReception.getPurchaseOrder().getStatus(),
                purchaseOrderReception.getStatus(),
                purchaseOrderReception.getPurchaseOrder().getId(),
                purchaseOrderReception.getGrocer() != null ? purchaseOrderReception.getGrocer().getUser().getPersonalInformation().getFullName() : "Nadie",
                purchaseOrderReception.getGrocer() != null ? purchaseOrderReception.getGrocer().getId() : null
        );
    }
}

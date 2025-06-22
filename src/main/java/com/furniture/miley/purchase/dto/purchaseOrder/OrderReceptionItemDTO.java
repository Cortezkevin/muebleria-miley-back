package com.furniture.miley.purchase.dto.purchaseOrder;

public record OrderReceptionItemDTO (
        boolean accept,
        PurchaseOrderDetailDTO purchaseOrderDetail
){
}

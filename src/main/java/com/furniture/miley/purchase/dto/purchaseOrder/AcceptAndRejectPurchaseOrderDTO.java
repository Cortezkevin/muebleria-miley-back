package com.furniture.miley.purchase.dto.purchaseOrder;

import java.util.List;

public record AcceptAndRejectPurchaseOrderDTO(
        String acceptConditions,
        String warehouseLocation,
        String rejectReason,
        String rejectConditions,
        String suggestions,
        List<String> acceptedOrderDetailIds
) {
}

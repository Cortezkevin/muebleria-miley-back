package com.furniture.miley.purchase.dto.guide;

import com.furniture.miley.purchase.model.EntryGuide;

import java.sql.Timestamp;

public record EntryGuideDTO(
        String id,
        Timestamp date,
        String productConditions,
        String grocer,
        String purchaseOrderId,
        String warehouse
) {
    public static EntryGuideDTO parseToDTO(EntryGuide entryGuide){
        return new EntryGuideDTO(
                entryGuide.getId(),
                entryGuide.getDate(),
                entryGuide.getProductConditions(),
                entryGuide.getGrocer().getUser().getPersonalInformation().getFullName(),
                entryGuide.getPurchaseOrder() != null ? entryGuide.getPurchaseOrder().getId() : "No aplica",
                entryGuide.getWarehouse().getLocation()
        );
    }
}

package com.furniture.miley.purchase.dto.guide;

import com.furniture.miley.purchase.dto.purchaseOrder.PurchaseOrderDTO;
import com.furniture.miley.purchase.model.EntryGuide;
import com.furniture.miley.warehouse.dto.warehouse.MinimalInventoryMovementDTO;

import java.sql.Timestamp;
import java.util.List;

public record DetailedEntryGuideDTO (
        String id,
        Timestamp date,
        String productConditions,
        String grocer,
        PurchaseOrderDTO purchaseOrder,
        List<MinimalInventoryMovementDTO> movementsList,
        String warehouse
){
    public static DetailedEntryGuideDTO parseToDTO(EntryGuide entryGuide){
        return new DetailedEntryGuideDTO(
                entryGuide.getId(),
                entryGuide.getDate(),
                entryGuide.getProductConditions(),
                entryGuide.getGrocer().getUser().getPersonalInformation().getFullName(),
                entryGuide.getPurchaseOrder() != null ? PurchaseOrderDTO.toDTO( entryGuide.getPurchaseOrder() ) : null,
                entryGuide.getInventoryMovementsList().stream().map( MinimalInventoryMovementDTO::parseToDTO ).toList(),
                entryGuide.getWarehouse().getLocation()
        );
    }
}

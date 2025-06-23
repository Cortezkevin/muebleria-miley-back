package com.furniture.miley.purchase.dto.guide;


import com.furniture.miley.sales.dto.order.OrderDTO;
import com.furniture.miley.warehouse.dto.warehouse.MinimalInventoryMovementDTO;
import com.furniture.miley.warehouse.model.ExitGuide;

import java.sql.Timestamp;
import java.util.List;

public record DetailedExitGuideDTO (
        String id,
        Timestamp date,
        String observations,
        String grocer,
        OrderDTO order,
        List<MinimalInventoryMovementDTO> movementsList,
        String warehouse
){
    public static DetailedExitGuideDTO parseToDTO(ExitGuide exitGuide){
        return new DetailedExitGuideDTO(
                exitGuide.getId(),
                exitGuide.getDate(),
                exitGuide.getObservations(),
                exitGuide.getGrocer().getUser().getPersonalInformation().getFullName(),
                exitGuide.getOrder() != null ? OrderDTO.toDTO( exitGuide.getOrder() ) : null,
                exitGuide.getInventoryMovementsList().stream().map( MinimalInventoryMovementDTO::parseToDTO ).toList(),
                exitGuide.getWarehouse().getLocation()
        );
    }
}

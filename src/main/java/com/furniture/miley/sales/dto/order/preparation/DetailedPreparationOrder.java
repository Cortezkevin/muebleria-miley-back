package com.furniture.miley.sales.dto.order.preparation;

import com.furniture.miley.sales.dto.order.DetailedOrderDTO;
import com.furniture.miley.sales.enums.PreparationStatus;
import com.furniture.miley.sales.model.order.OrderPreparation;

import java.sql.Timestamp;

public record DetailedPreparationOrder(
        String id,
        DetailedOrderDTO order,
        Timestamp createdDate,
        Timestamp startDate,
        Timestamp preparedDate,
        Timestamp completedDate,
        PreparationStatus status
) {
    public static DetailedPreparationOrder toDTO(OrderPreparation orderPreparation) {
        return new DetailedPreparationOrder(
                orderPreparation.getId(),
                DetailedOrderDTO.toDTO( orderPreparation.getOrder()),
                orderPreparation.getCreatedDate(),
                orderPreparation.getStartDate(),
                orderPreparation.getPreparedDate(),
                orderPreparation.getCompletedDate(),
                orderPreparation.getStatus()
        );
    }
}

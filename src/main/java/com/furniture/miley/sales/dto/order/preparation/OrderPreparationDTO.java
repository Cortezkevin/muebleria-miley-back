package com.furniture.miley.sales.dto.order.preparation;

import com.furniture.miley.sales.enums.OrderStatus;
import com.furniture.miley.sales.enums.PreparationStatus;
import com.furniture.miley.sales.model.order.OrderPreparation;
import com.furniture.miley.warehouse.dto.grocer.GrocerDTO;

import java.sql.Timestamp;

public record OrderPreparationDTO(
        String id,
        String userIdFromGrocer,
        String orderId,
        GrocerDTO grocer,
        Timestamp createdDate,
        Timestamp startDate,
        Timestamp preparedDate,
        Timestamp completedDate,
        OrderStatus orderStatus,
        PreparationStatus status
) {
    public static OrderPreparationDTO toDTO(OrderPreparation orderPreparation) {
        return new OrderPreparationDTO(
                orderPreparation.getId(),
                orderPreparation.getGrocer() != null ? orderPreparation.getGrocer().getUser().getId() : null,
                orderPreparation.getOrder().getId(),
                orderPreparation.getGrocer() != null ? GrocerDTO.toDTO(orderPreparation.getGrocer()) : null,
                orderPreparation.getCreatedDate(),
                orderPreparation.getStartDate(),
                orderPreparation.getPreparedDate(),
                orderPreparation.getCompletedDate(),
                orderPreparation.getOrder().getStatus(),
                orderPreparation.getStatus()
        );
    }
}

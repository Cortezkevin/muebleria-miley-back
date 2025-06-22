package com.furniture.miley.sales.dto.order.shipping;

import com.furniture.miley.sales.dto.order.DetailedOrderDTO;
import com.furniture.miley.sales.enums.ShippingStatus;
import com.furniture.miley.sales.model.order.OrderShipping;

import java.sql.Timestamp;

public record DetailedShippingOrder(
        String id,
        String preparedBy,
        DetailedOrderDTO order,
        Timestamp createdDate,
        Timestamp startDate,
        Timestamp preparedDate,
        Timestamp shippingDate,
        Timestamp completedDate,
        String exitGuideId,
        ShippingStatus status
) {
    public static DetailedShippingOrder toDTO(OrderShipping orderShipping) {
        return new DetailedShippingOrder(
                orderShipping.getId(),
                orderShipping.getPreparedBy(),
                DetailedOrderDTO.toDTO(orderShipping.getOrder()),
                orderShipping.getCreatedDate(),
                orderShipping.getStartDate(),
                orderShipping.getPreparedDate(),
                orderShipping.getShippingDate(),
                orderShipping.getCompletedDate(),
                orderShipping.getOrder().getExitGuide().getId(),
                orderShipping.getStatus()
        );
    }
}

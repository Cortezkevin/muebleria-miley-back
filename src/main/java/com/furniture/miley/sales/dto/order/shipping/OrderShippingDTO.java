package com.furniture.miley.sales.dto.order.shipping;
import com.furniture.miley.sales.enums.ShippingStatus;
import com.furniture.miley.sales.model.order.OrderShipping;
import com.furniture.miley.warehouse.dto.carrier.CarrierDTO;

import java.sql.Timestamp;

public record OrderShippingDTO (
        String id,
        String userIdFromCarrier,
        String orderId,
        CarrierDTO carrier,
        String preparedBy,
        String address,
        Timestamp createdDate,
        Timestamp startDate,
        Timestamp preparedDate,
        Timestamp shippingDate,
        Timestamp completedDate,
        ShippingStatus status
){
    public static OrderShippingDTO toDTO(OrderShipping orderShipping) {
        return new OrderShippingDTO(
                orderShipping.getId(),
                orderShipping.getCarrier() != null ? orderShipping.getCarrier().getUser().getId() : null,
                orderShipping.getOrder().getId(),
                orderShipping.getCarrier() != null ? CarrierDTO.toDTO(orderShipping.getCarrier()) : null,
                orderShipping.getPreparedBy(),
                orderShipping.getAddress(),
                orderShipping.getCreatedDate(),
                orderShipping.getStartDate(),
                orderShipping.getPreparedDate(),
                orderShipping.getShippingDate(),
                orderShipping.getCompletedDate(),
                orderShipping.getStatus()
        );
    }
}

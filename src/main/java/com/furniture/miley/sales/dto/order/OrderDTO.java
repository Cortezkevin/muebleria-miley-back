package com.furniture.miley.sales.dto.order;

import com.furniture.miley.sales.enums.OrderStatus;
import com.furniture.miley.sales.enums.PreparationStatus;
import com.furniture.miley.sales.enums.ShippingStatus;
import com.furniture.miley.sales.model.order.Order;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record OrderDTO(
        String id,
        BigDecimal total,
        String user,
        String shippingAddress,
        Timestamp createdDate,
        Timestamp cancelledDate,
        Timestamp completedDate,
        //PaymentMethod paymentMethod,
        PreparationStatus preparationStatus,
        ShippingStatus shippingStatus,
        OrderStatus status
) {
    public static OrderDTO toDTO(Order order){
        return new OrderDTO(
                order.getId(),
                order.getTotal(),
                order.getUser().getPersonalInformation().getFullName(),
                order.getShippingAddress(),
                order.getCreatedDate(),
                order.getCancelledDate(),
                order.getCompletedDate(),
                //order.getPaymentMethod(),
                order.getOrderPreparation() != null ? order.getOrderPreparation().getStatus() : PreparationStatus.PENDIENTE,
                order.getOrderShipping() != null ? order.getOrderShipping().getStatus() : ShippingStatus.PENDIENTE,
                order.getStatus()
        );
    }
}

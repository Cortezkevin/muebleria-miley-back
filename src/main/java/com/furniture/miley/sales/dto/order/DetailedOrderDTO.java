package com.furniture.miley.sales.dto.order;

import com.furniture.miley.sales.dto.order.preparation.OrderPreparationDTO;
import com.furniture.miley.sales.dto.order.shipping.OrderShippingDTO;
import com.furniture.miley.sales.enums.OrderStatus;
import com.furniture.miley.sales.enums.PaymentMethod;
import com.furniture.miley.sales.model.order.Order;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public record DetailedOrderDTO(
        String id,
        String note,
        BigDecimal subtotal,
        BigDecimal shippingCost,
        BigDecimal tax,
        BigDecimal discount,
        BigDecimal total,
        UserOrderDTO user,
        String shippingAddress,
        String specificAddress,
        Timestamp createdDate,
        Timestamp cancelledDate,
        Timestamp completedDate,
        PaymentMethod paymentMethod,
        OrderPreparationDTO preparation,
        OrderShippingDTO shipping,
        OrderStatus status,
        List<OrderDetailDTO> orderDetails,
        String invoiceUrl
) {
    public static DetailedOrderDTO toDTO(Order order){
        return new DetailedOrderDTO(
                order.getId(),
                order.getNote(),
                order.getSubtotal(),
                order.getShippingCost(),
                order.getTax(),
                order.getDiscount(),
                order.getTotal(),
                UserOrderDTO.toDTO(order.getUser()),
                order.getShippingAddress(),
                order.getSpecificAddress(),
                order.getCreatedDate(),
                order.getCancelledDate(),
                order.getCompletedDate(),
                order.getPaymentMethod(),
                OrderPreparationDTO.toDTO(order.getOrderPreparation()),
                order.getOrderShipping() != null ? OrderShippingDTO.toDTO(order.getOrderShipping()) : null,
                order.getStatus(),
                order.getOrderDetails().stream().map(OrderDetailDTO::toDTO).toList(),
                order.getInvoice().getUrl()
        );
    }
}

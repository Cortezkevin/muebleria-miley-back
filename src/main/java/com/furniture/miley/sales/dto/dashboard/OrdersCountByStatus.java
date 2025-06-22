package com.furniture.miley.sales.dto.dashboard;

import com.furniture.miley.sales.enums.OrderStatus;

public record OrdersCountByStatus(
        OrderStatus status,
        Long amount
) {
}

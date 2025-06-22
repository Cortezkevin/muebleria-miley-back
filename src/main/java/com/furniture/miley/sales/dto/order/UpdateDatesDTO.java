package com.furniture.miley.sales.dto.order;

import java.sql.Timestamp;

public record UpdateDatesDTO(
        String orderId,
        Timestamp createdDate,
        Timestamp preparationDate,
        Timestamp shippingDate,
        Timestamp completedDate,
        Double distance
) {
}

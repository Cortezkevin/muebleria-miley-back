package com.furniture.miley.sales.dto.order.preparation;

public record CompletedOrderPreparationDTO(
        String orderPreparationId,
        String observations,
        String warehouse
) {
}

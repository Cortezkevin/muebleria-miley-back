package com.furniture.miley.sales.dto.dashboard;

public record SalesByUser(
        String client,
        Long salesTotal
) {
}

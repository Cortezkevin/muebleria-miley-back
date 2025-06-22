package com.furniture.miley.sales.dto.dashboard;

import java.math.BigDecimal;

public record SalesByMonth(
        Integer month,
        BigDecimal sales
) {
}

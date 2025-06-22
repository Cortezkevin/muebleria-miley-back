package com.furniture.miley.sales.dto.dashboard;

import java.math.BigDecimal;
import java.util.List;

public record SalesDashboard(
        Integer products,
        Integer categories,
        Integer subcategories,
        Integer users,
        Integer orders,
        BigDecimal totalSales,
        List<OrderDurationPerDistanceAVG> avgDurationByDistanceRange,
        List<OrdersCountByStatus> ordersCountByStatus,
        List<SalesByProduct> topProductByYear,
        List<SalesByUser> topUser,
        List<SalesByMonth> salesByMonth
) {
}

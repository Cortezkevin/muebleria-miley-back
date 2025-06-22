package com.furniture.miley.sales.dto.dashboard;

public record OrderDurationPerDistanceAVG(
        String distanceRange,
        Double avgDurationInDays
) {
}

package com.furniture.miley.warehouse.dto.carrier;

import com.furniture.miley.delivery.enums.CarrierStatus;

public record NewCarrierDTO(
        String userId,
        String plateCode,
        CarrierStatus status
) {
}

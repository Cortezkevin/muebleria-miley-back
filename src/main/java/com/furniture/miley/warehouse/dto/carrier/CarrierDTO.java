package com.furniture.miley.warehouse.dto.carrier;

import com.furniture.miley.delivery.enums.CarrierStatus;
import com.furniture.miley.delivery.model.Carrier;

public record CarrierDTO(
        String id,
        String fullName,
        String email,
        String phone,
        String plateCode,
        String userId,
        CarrierStatus status
) {
    public static CarrierDTO toDTO(Carrier carrier){
        return new CarrierDTO(
                carrier.getId(),
                carrier.getUser().getPersonalInformation().getFullName(),
                carrier.getUser().getEmail(),
                carrier.getUser().getPersonalInformation().getPhone(),
                carrier.getCodePlate(),
                carrier.getUser().getId(),
                carrier.getStatus()
        );
    }
}

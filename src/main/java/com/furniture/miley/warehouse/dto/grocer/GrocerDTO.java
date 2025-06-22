package com.furniture.miley.warehouse.dto.grocer;


import com.furniture.miley.warehouse.enums.GrocerStatus;
import com.furniture.miley.warehouse.model.Grocer;

public record GrocerDTO(
        String id,
        String fullName,
        String email,
        String phone,
        String userId,
        GrocerStatus status
) {
    public static GrocerDTO toDTO(Grocer grocer){
        return new GrocerDTO(
                grocer.getId(),
                grocer.getUser().getPersonalInformation().getFullName(),
                grocer.getUser().getEmail(),
                grocer.getUser().getPersonalInformation().getPhone(),
                grocer.getUser().getId(),
                grocer.getStatus()
        );
    }
}

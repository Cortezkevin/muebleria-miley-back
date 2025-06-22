package com.furniture.miley.sales.dto.order;

import com.furniture.miley.security.model.User;

public record UserOrderDTO(
        String fullName,
        String email,
        String phone
) {
    public static UserOrderDTO toDTO(User user){
        return new UserOrderDTO(
                user.getPersonalInformation().getFullName(),
                user.getEmail(),
                user.getPersonalInformation().getPhone()
        );
    }
}

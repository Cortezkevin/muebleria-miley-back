package com.furniture.miley.purchase.dto.purchaseOrder;

import com.furniture.miley.security.model.User;

public record PurchaseUserDTO(
        String id,
        String fullName,
        String email
) {
    public static PurchaseUserDTO parseToDTO(User user){
        return new PurchaseUserDTO(
                user.getId(),
                user.getPersonalInformation().getFullName(),
                user.getEmail()
        );
    }
}

package com.furniture.miley.security.dto;

import com.furniture.miley.profile.dto.address.MemoryAddressDTO;
import com.furniture.miley.sales.dto.cart.MemoryCartDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record NewUserDTO (
        @NotBlank(message = "Required") String firstName,
        @NotBlank(message = "Required") String lastName,
        @Email(message = "Invalid") String email,
        @NotBlank(message = "Required") String password,
        String notificationMobileToken,
        String notificationWebToken,
        MemoryCartDTO memoryCart,
        MemoryAddressDTO memoryAddress,
        Boolean isAdmin
){}

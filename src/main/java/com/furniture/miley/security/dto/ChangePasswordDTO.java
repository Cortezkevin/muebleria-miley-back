package com.furniture.miley.security.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordDTO(
        @NotBlank( message = "Required") String password,
        @NotBlank( message = "Required") String confirmPassword,
        @NotBlank( message = "Required") String tokenPassword
) {
}

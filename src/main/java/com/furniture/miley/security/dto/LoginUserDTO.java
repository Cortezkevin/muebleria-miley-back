package com.furniture.miley.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginUserDTO(
        @NotBlank(message = "Required") @Email( message = "Invalid") String email,
        @NotBlank(message = "Required") String password
){
}

package com.furniture.miley.profile.dto.user;

import com.furniture.miley.security.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CreateUserDTO(
        @NotBlank(message = "Required") String firstName,
        @NotBlank(message = "Required") String lastName,
        @Email(message = "Invalid") String email,
        @NotBlank(message = "Required") String password,
        UserStatus userStatus,
        List<String> roles
) {
}

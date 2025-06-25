package com.furniture.miley.profile.dto.user;

import com.furniture.miley.security.enums.Status;

import java.util.List;

public record UpdateUserDTO (
        String userId,
        String firstName,
        String lastName,
        String email,
        Status status,
        List<String> roles
){
}

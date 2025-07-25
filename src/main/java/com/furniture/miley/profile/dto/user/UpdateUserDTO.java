package com.furniture.miley.profile.dto.user;

import com.furniture.miley.security.enums.UserStatus;

import java.util.List;

public record UpdateUserDTO (
        String userId,
        String firstName,
        String lastName,
        String email,
        UserStatus userStatus,
        List<String> roles
){
}

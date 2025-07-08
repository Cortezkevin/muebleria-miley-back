package com.furniture.miley.security.dto;

import com.furniture.miley.security.model.User;

import java.util.List;

public record SessionDTO(
        String token,
        String id,
        String email,
        String photo,
        List<String> roles
) {
    public static SessionDTO toDTO(User user, String token){
        return new SessionDTO(
                token,
                user.getId(),
                user.getEmail(),
                user.getPersonalInformation().getPhotoUrl(),
                user.getRoles().stream().map(r -> r.getRolName().name()).toList()
        );
    }
}

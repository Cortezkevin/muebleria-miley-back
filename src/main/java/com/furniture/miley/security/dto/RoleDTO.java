package com.furniture.miley.security.dto;

import com.furniture.miley.security.model.Role;

public record RoleDTO(
        String value,
        String key
){
    public static RoleDTO parseToDTO(Role role){
        return new RoleDTO(
                role.getRolName().getCapitalizedName(),
                role.getRolName().name()
        );
    }
}

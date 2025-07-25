package com.furniture.miley.security.dto;

import com.furniture.miley.profile.dto.address.AddressDTO;
import com.furniture.miley.security.enums.ResourceStatus;
import com.furniture.miley.security.enums.RolName;
import com.furniture.miley.security.enums.UserStatus;
import com.furniture.miley.security.model.Role;
import com.furniture.miley.security.model.User;
import com.furniture.miley.warehouse.dto.carrier.CarrierDTO;
import com.furniture.miley.warehouse.dto.grocer.GrocerDTO;

import java.util.Set;
import java.util.stream.Collectors;

public record MinimalUserDTO(
        String id,
        String firstName,
        String lastName,
        String email,
        String photoUrl,
        Set<String> roles,
        UserStatus userStatus,
        ResourceStatus resourceStatus
) {
    public static MinimalUserDTO toDTO( User user){
        return new MinimalUserDTO(
                user.getId(),
                user.getPersonalInformation().getFirstName(),
                user.getPersonalInformation().getLastName(),
                user.getEmail(),
                user.getPersonalInformation().getPhotoUrl() != null ? user.getPersonalInformation().getPhotoUrl() : "https://st3.depositphotos.com/6672868/13701/v/450/depositphotos_137014128-stock-illustration-user-profile-icon.jpg",
                user.getRoles().stream().map(Role::getRolName).map(RolName::name).collect(Collectors.toSet()),
                user.getUserStatus(),
                user.getResourceStatus()
        );
    }
}

package com.furniture.miley.security.dto;

import com.furniture.miley.profile.dto.address.AddressDTO;
import com.furniture.miley.security.enums.Status;
import com.furniture.miley.security.model.MainUser;
import com.furniture.miley.profile.model.PersonalInformation;
import com.furniture.miley.security.model.Role;
import com.furniture.miley.security.model.User;
import com.furniture.miley.security.enums.RolName;

import java.util.Set;
import java.util.stream.Collectors;

public record UserDTO(
        String id,
        String firstName,
        String lastName,
        String email,
        Set<String> roles,
        Status status,
        ProfileDTO profile
){
    public static UserDTO toDTO(User user, MainUser mainUser) {
        return new UserDTO(
                user.getId(),
                user.getPersonalInformation().getFirstName(),
                user.getPersonalInformation().getLastName(),
                mainUser.getEmail(),
                mainUser.getRoles().stream().map(RolName::name).collect(Collectors.toSet()),
                user.getStatus(),
                new ProfileDTO(
                        user.getPersonalInformation().getBirthdate() != null ? user.getPersonalInformation().getBirthdate().toString() : "",
                        user.getPersonalInformation().getAddress() != null ? AddressDTO.toDTO(user.getPersonalInformation().getAddress()) : null,
                        user.getPersonalInformation().getPhone() != null ? user.getPersonalInformation().getPhone() : ""
                )
        );
    }

    public static UserDTO toDTO(User user, PersonalInformation personalInformation){
        return new UserDTO(
                user.getId(),
                personalInformation.getFirstName(),
                personalInformation.getLastName(),
                user.getEmail(),
                user.getRoles().stream().map(Role::getRolName).map(RolName::name).collect(Collectors.toSet()),
                user.getStatus(),
                new ProfileDTO(
                        user.getPersonalInformation().getBirthdate() != null ? user.getPersonalInformation().getBirthdate().toString() : "",
                        user.getPersonalInformation().getAddress() != null ? AddressDTO.toDTO( user.getPersonalInformation().getAddress() ) : null,
                        user.getPersonalInformation().getPhone() != null ? user.getPersonalInformation().getPhone() : ""
                )
        );
    }

    public static UserDTO toDTO( User user){
        return new UserDTO(
                user.getId(),
                user.getPersonalInformation().getFirstName(),
                user.getPersonalInformation().getLastName(),
                user.getEmail(),
                user.getRoles().stream().map(Role::getRolName).map(RolName::name).collect(Collectors.toSet()),
                user.getStatus(),
                new ProfileDTO(
                        user.getPersonalInformation().getBirthdate() != null ? user.getPersonalInformation().getBirthdate().toString() : "",
                        user.getPersonalInformation().getAddress() != null ? AddressDTO.toDTO( user.getPersonalInformation().getAddress() ) : null,
                        user.getPersonalInformation().getPhone() != null ? user.getPersonalInformation().getPhone() : ""
                )
        );
    }
}

package com.furniture.miley.security.dto;

import com.furniture.miley.dto.address.AddressDTO;
import com.furniture.miley.model.MainUser;
import com.furniture.miley.model.PersonalInformation;
import com.furniture.miley.model.Role;
import com.furniture.miley.model.User;
import com.furniture.miley.security.enums.RolName;

import java.util.Set;
import java.util.stream.Collectors;

public record UserDTO(
        String id,
        String firstName,
        String lastName,
        String email,
        Set<String> roles,
        ProfileDTO profile
){
    public static UserDTO parseToDto(User user, MainUser mainUser) {
        return new UserDTO(
                user.getId(),
                user.getPersonalInformation().getFirstName(),
                user.getPersonalInformation().getLastName(),
                mainUser.getEmail(),
                mainUser.getRoles().stream().map(RolName::name).collect(Collectors.toSet()),
                new ProfileDTO(
                        user.getPersonalInformation().getBirthdate() != null ? user.getPersonalInformation().getBirthdate().toString() : "",
                        user.getPersonalInformation().getAddress() != null ? AddressDTO.parseToDTO(user.getPersonalInformation().getAddress()) : null,
                        user.getPersonalInformation().getPhone() != null ? user.getPersonalInformation().getPhone() : ""
                )
        );
    }

    public static UserDTO parseToDTO(User user, PersonalInformation personalInformation){
        return new UserDTO(
                user.getId(),
                personalInformation.getFirstName(),
                personalInformation.getLastName(),
                user.getEmail(),
                user.getRoles().stream().map(Role::getRolName).map(RolName::name).collect(Collectors.toSet()),
                new ProfileDTO(
                        user.getPersonalInformation().getBirthdate() != null ? user.getPersonalInformation().getBirthdate().toString() : "",
                        user.getPersonalInformation().getAddress() != null ? AddressDTO.parseToDTO( user.getPersonalInformation().getAddress() ) : null,
                        user.getPersonalInformation().getPhone() != null ? user.getPersonalInformation().getPhone() : ""
                )
        );
    }

    public static UserDTO parseToDTO( User user){
        return new UserDTO(
                user.getId(),
                user.getPersonalInformation().getFirstName(),
                user.getPersonalInformation().getLastName(),
                user.getEmail(),
                user.getRoles().stream().map(Role::getRolName).map(RolName::name).collect(Collectors.toSet()),
                new ProfileDTO(
                        user.getPersonalInformation().getBirthdate() != null ? user.getPersonalInformation().getBirthdate().toString() : "",
                        user.getPersonalInformation().getAddress() != null ? AddressDTO.parseToDTO( user.getPersonalInformation().getAddress() ) : null,
                        user.getPersonalInformation().getPhone() != null ? user.getPersonalInformation().getPhone() : ""
                )
        );
    }
}

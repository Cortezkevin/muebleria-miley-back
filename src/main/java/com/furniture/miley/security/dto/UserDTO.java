package com.furniture.miley.security.dto;

import com.furniture.miley.profile.dto.address.AddressDTO;
import com.furniture.miley.security.enums.UserStatus;
import com.furniture.miley.security.model.MainUser;
import com.furniture.miley.profile.model.PersonalInformation;
import com.furniture.miley.security.model.Role;
import com.furniture.miley.security.model.User;
import com.furniture.miley.security.enums.RolName;
import com.furniture.miley.warehouse.dto.carrier.CarrierDTO;
import com.furniture.miley.warehouse.dto.grocer.GrocerDTO;

import java.util.Set;
import java.util.stream.Collectors;

public record UserDTO(
        String id,
        String firstName,
        String lastName,
        String email,
        String photoUrl,
        Set<String> roles,
        UserStatus userStatus,
        ProfileDTO profile,
        Object roleExtraData
){
    public static UserDTO toDTO(User user, MainUser mainUser) {
        return new UserDTO(
                user.getId(),
                user.getPersonalInformation().getFirstName(),
                user.getPersonalInformation().getLastName(),
                mainUser.getEmail(),
                user.getPersonalInformation().getPhotoUrl() != null ? user.getPersonalInformation().getPhotoUrl() : "https://st3.depositphotos.com/6672868/13701/v/450/depositphotos_137014128-stock-illustration-user-profile-icon.jpg",
                mainUser.getRoles().stream().map(RolName::name).collect(Collectors.toSet()),
                user.getUserStatus(),
                new ProfileDTO(
                        user.getPersonalInformation().getBirthdate() != null ? user.getPersonalInformation().getBirthdate().toString() : "",
                        user.getPersonalInformation().getAddress() != null ? AddressDTO.toDTO(user.getPersonalInformation().getAddress()) : null,
                        user.getPersonalInformation().getPhone() != null ? user.getPersonalInformation().getPhone() : ""
                ),
                user.getGrocer() != null ? GrocerDTO.toDTO(user.getGrocer()) : user.getCarrier() != null ? CarrierDTO.toDTO(user.getCarrier()) : null
        );
    }

    public static UserDTO toDTO(User user, PersonalInformation personalInformation){
        return new UserDTO(
                user.getId(),
                personalInformation.getFirstName(),
                personalInformation.getLastName(),
                user.getEmail(),
                user.getPersonalInformation().getPhotoUrl() != null ? user.getPersonalInformation().getPhotoUrl() : "https://st3.depositphotos.com/6672868/13701/v/450/depositphotos_137014128-stock-illustration-user-profile-icon.jpg",
                user.getRoles().stream().map(Role::getRolName).map(RolName::name).collect(Collectors.toSet()),
                user.getUserStatus(),
                new ProfileDTO(
                        user.getPersonalInformation().getBirthdate() != null ? user.getPersonalInformation().getBirthdate().toString() : "",
                        user.getPersonalInformation().getAddress() != null ? AddressDTO.toDTO( user.getPersonalInformation().getAddress() ) : null,
                        user.getPersonalInformation().getPhone() != null ? user.getPersonalInformation().getPhone() : ""
                ),
                user.getGrocer() != null ? GrocerDTO.toDTO(user.getGrocer()) : user.getCarrier() != null ? CarrierDTO.toDTO(user.getCarrier()) : null
        );
    }

    public static UserDTO toDTO( User user){
        return new UserDTO(
                user.getId(),
                user.getPersonalInformation().getFirstName(),
                user.getPersonalInformation().getLastName(),
                user.getEmail(),
                user.getPersonalInformation().getPhotoUrl() != null ? user.getPersonalInformation().getPhotoUrl() : "https://st3.depositphotos.com/6672868/13701/v/450/depositphotos_137014128-stock-illustration-user-profile-icon.jpg",
                user.getRoles().stream().map(Role::getRolName).map(RolName::name).collect(Collectors.toSet()),
                user.getUserStatus(),
                new ProfileDTO(
                        user.getPersonalInformation().getBirthdate() != null ? user.getPersonalInformation().getBirthdate().toString() : "",
                        user.getPersonalInformation().getAddress() != null ? AddressDTO.toDTO( user.getPersonalInformation().getAddress() ) : null,
                        user.getPersonalInformation().getPhone() != null ? user.getPersonalInformation().getPhone() : ""
                ),
                user.getGrocer() != null ? GrocerDTO.toDTO(user.getGrocer()) : user.getCarrier() != null ? CarrierDTO.toDTO(user.getCarrier()) : null
        );
    }
}

package com.furniture.miley.profile.dto.information;

import com.furniture.miley.profile.model.PersonalInformation;

import java.time.LocalDate;

public record PersonalInformationDTO(
        String firstName,
        String lastName,
        String phone,
        LocalDate birthdate
) {
    public static PersonalInformationDTO parseToDTO(PersonalInformation personalInformationUpdated) {
        return new PersonalInformationDTO(
                personalInformationUpdated.getFirstName(),
                personalInformationUpdated.getLastName(),
                personalInformationUpdated.getPhone(),
                personalInformationUpdated.getBirthdate()
        );
    }
}

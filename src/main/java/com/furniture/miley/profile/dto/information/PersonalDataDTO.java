package com.furniture.miley.profile.dto.information;

import com.furniture.miley.profile.model.PersonalInformation;

import java.time.LocalDate;

public record PersonalDataDTO(
        String firstName,
        String lastName,
        String phone,
        LocalDate birthdate
) {
    public static PersonalDataDTO parseToDTO(PersonalInformation personalInformationUpdated) {
        return new PersonalDataDTO(
                personalInformationUpdated.getFirstName(),
                personalInformationUpdated.getLastName(),
                personalInformationUpdated.getPhone(),
                personalInformationUpdated.getBirthdate()
        );
    }
}

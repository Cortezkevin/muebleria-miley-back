package com.furniture.miley.profile.service;

import com.furniture.miley.config.cloudinary.service.CloudinaryService;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.profile.dto.user.UpdateProfile;
import com.furniture.miley.profile.model.PersonalInformation;
import com.furniture.miley.profile.repository.PersonalInformationRepository;
import com.furniture.miley.security.dto.UserDTO;
import com.furniture.miley.security.model.User;
import com.furniture.miley.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PersonalInformationService {

    private final PersonalInformationRepository personalInformationRepository;

    private final UserService userService;
    private final CloudinaryService cloudinaryService;

    public PersonalInformation findById(String id) throws ResourceNotFoundException {
        return personalInformationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de usuario no encontrado", "PersonalInformation"));
    }

    public PersonalInformation findByUser(User user) throws ResourceNotFoundException {
        return personalInformationRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de usuario no encontrado", "PersonalInformation"));
    }

    public UserDTO updateProfile(UpdateProfile updateProfile, File file) throws ResourceNotFoundException, IOException {
        User user = userService.findById( updateProfile.userId() );
        PersonalInformation personalInformation = this.findByUser( user );
        if( personalInformation != null ){

            if( file != null){
                String photoUrl = cloudinaryService.uploadAndGetUrl("profile",user.getId(), file);
                personalInformation.setPhotoUrl(photoUrl);
            }

            user.setEmail(updateProfile.email() );
            personalInformation.setFirstName(updateProfile.firstName());
            personalInformation.setLastName(updateProfile.lastName());
            personalInformation.setPhone(updateProfile.phone());
            if( updateProfile.birthdate() != null && !updateProfile.birthdate().equals("")){
                personalInformation.setBirthdate(LocalDate.parse(updateProfile.birthdate()));
            }
            user.setPersonalInformation( personalInformation );
        }

        return UserDTO.toDTO(userService.save( user ));
    }

}

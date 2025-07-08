package com.furniture.miley.profile.controller;

import com.furniture.miley.commons.constants.ResponseMessage;
import com.furniture.miley.commons.dto.SuccessResponseDTO;
import com.furniture.miley.config.cloudinary.utils.UploadUtils;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.profile.dto.address.AddressDTO;
import com.furniture.miley.profile.dto.information.PersonalDataDTO;
import com.furniture.miley.profile.dto.user.UpdateProfile;
import com.furniture.miley.profile.service.PersonalInformationService;
import com.furniture.miley.security.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class PersonalInformationController {
    private final PersonalInformationService personalInformationService;

    @GetMapping("/fromSession")
    public ResponseEntity<SuccessResponseDTO<PersonalDataDTO>> getFromSession() throws ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.PRODUCT_ADDED_TO_CART,
                        HttpStatus.OK.name(),
                        personalInformationService.getFromSession()
                )
        );
    }


    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    @PutMapping( consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<SuccessResponseDTO<UserDTO>> update(
            @RequestPart(name = "file", required = false) MultipartFile multipartFile,
            @RequestPart("body") String bodyString
    ) throws IOException, ResourceNotFoundException {
        UpdateProfile updateProfile = UploadUtils.convertStringToObject( bodyString, UpdateProfile.class );
        File fileToUpload = UploadUtils.getFileFromMultipartFile( multipartFile );
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.UPDATED,
                        HttpStatus.OK.name(),
                        personalInformationService.updateProfile( updateProfile, fileToUpload )
                )
        );
    }
}

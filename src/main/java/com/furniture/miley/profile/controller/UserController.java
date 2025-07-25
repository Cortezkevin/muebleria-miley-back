package com.furniture.miley.profile.controller;

import com.furniture.miley.commons.constants.ResponseMessage;
import com.furniture.miley.commons.dto.SuccessResponseDTO;
import com.furniture.miley.config.cloudinary.utils.UploadUtils;
import com.furniture.miley.exception.customexception.ResourceDuplicatedException;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.profile.dto.user.CreateUserDTO;
import com.furniture.miley.profile.dto.user.UpdateProfile;
import com.furniture.miley.profile.dto.user.UpdateUserDTO;
import com.furniture.miley.profile.service.PersonalInformationService;
import com.furniture.miley.security.dto.MinimalUserDTO;
import com.furniture.miley.security.dto.RoleDTO;
import com.furniture.miley.security.dto.UserDTO;
import com.furniture.miley.security.service.RoleService;
import com.furniture.miley.security.service.UserService;
import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final PersonalInformationService personalInformationService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<SuccessResponseDTO<List<MinimalUserDTO>>> getAll(){
        List<MinimalUserDTO> userDTOList = userService.getAll();
        return userDTOList.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(
                        new SuccessResponseDTO<>(
                                ResponseMessage.SUCCESS,
                                HttpStatus.OK.name(),
                                userDTOList
                        )
        );
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/roles")
    public ResponseEntity<SuccessResponseDTO<List<RoleDTO>>> getAllRoles(){
        List<RoleDTO> roleDTOList = roleService.getRoles();
        return roleDTOList.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        roleDTOList
                )
        );
    }

    @GetMapping("/extraRoleData")
    public ResponseEntity<SuccessResponseDTO<Object>> getExtraRoleData() throws ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        userService.getExtraRoleData()
                )
        );
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<SuccessResponseDTO<UserDTO>> create(
            @Valid @RequestBody CreateUserDTO createUserDTO
            ) throws ResourceDuplicatedException, ResourceNotFoundException, StripeException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.CREATED,
                        HttpStatus.CREATED.name(),
                        userService.create( createUserDTO )
                )
        );
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping
    public ResponseEntity<SuccessResponseDTO<UserDTO>> update(
            @RequestBody UpdateUserDTO updateUserDTO
            ) throws ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.UPDATED,
                        HttpStatus.OK.name(),
                        userService.update( updateUserDTO )
                )
        );
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PutMapping(value = "/profile", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
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

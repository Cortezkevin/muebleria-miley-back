package com.furniture.miley.security.controller;

import com.furniture.miley.commons.constants.ResponseMessage;
import com.furniture.miley.commons.dto.ResponseDTO;
import com.furniture.miley.commons.dto.SuccessResponseDTO;
import com.furniture.miley.exception.customexception.NotMatchPasswordsException;
import com.furniture.miley.security.dto.NewUserDTO;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.security.dto.ChangePasswordDTO;
import com.furniture.miley.security.dto.JwtTokenDTO;
import com.furniture.miley.security.dto.LoginUserDTO;
import com.furniture.miley.security.dto.UserDTO;
import com.furniture.miley.security.service.AuthService;
import com.furniture.miley.security.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;


    @GetMapping("/getUserFromToken")
    public ResponseEntity<SuccessResponseDTO<UserDTO>> getUserFromToken(@RequestHeader(name = "Authorization") String tokenHeader){
        String token = tokenHeader.length() > 7 ? tokenHeader.substring(7) : "no token";
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        authService.getUserFromToken(token)
                )
        );
    }

    @PostMapping("/roles")
    public ResponseEntity<SuccessResponseDTO<String>> createRoles(){
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        authService.createRoles()
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponseDTO<JwtTokenDTO>> loginUser(@Valid @RequestBody LoginUserDTO loginUserDTO){
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.LOGGED,
                        HttpStatus.OK.name(),
                        authService.loginUser(loginUserDTO)
                )
        );
    }

    @PostMapping("/register")
    public ResponseEntity<SuccessResponseDTO<JwtTokenDTO>> registerUser(@RequestBody NewUserDTO newUserDTO){
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.REGISTER,
                        HttpStatus.CREATED.name(),
                        authService.registerUser(newUserDTO)
                )
        );
    }

    @GetMapping("/sendConfirmationEmail")
    public ResponseEntity<ResponseDTO> confirmationEmail(@RequestParam(name = "to") String to) throws MessagingException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.EMAIL_SENT,
                        HttpStatus.OK.name(),
                        emailService.sendHtmlTemplateEmail(to)
                )
        );
    }

    @PutMapping("/changePassword")
    public ResponseEntity<SuccessResponseDTO<String>> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) throws ResourceNotFoundException, NotMatchPasswordsException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.PASSWORD_UPDATED,
                        HttpStatus.OK.name(),
                        authService.changePassword(changePasswordDTO)
                )
        );
    }
}

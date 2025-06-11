package com.furniture.miley.security.controller;


import com.furniture.miley.dto.NewUserDTO;
import com.furniture.miley.dto.ResponseWrapperDTO;
import com.furniture.miley.security.dto.JwtTokenDTO;
import com.furniture.miley.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseWrapperDTO<JwtTokenDTO>> registerUser(@RequestBody NewUserDTO newUserDTO){
        return ResponseEntity.ok( userService.registerUser(newUserDTO) );
    }
}

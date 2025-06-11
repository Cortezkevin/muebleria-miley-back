package com.furniture.miley.security.dto;

public record JwtTokenDTO (
    String token,
    UserDTO user
){}

package com.furniture.miley.exception.customexception;

import lombok.Getter;

public class InvalidCredentialsException extends Exception {
    @Getter
    private String password;
    @Getter
    private String email;
    public InvalidCredentialsException(String message, String email, String password) {
        super(message);
        this.email = email;
        this.password = password;
    }
}

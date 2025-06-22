package com.furniture.miley.exception.customexception;

import lombok.Getter;

public class NotMatchPasswordsException extends Exception {
    @Getter
    private String password;
    @Getter
    private String confirmPassword;

    public NotMatchPasswordsException(String message, String password, String confirmPassword) {
        super(message);
        this.password = password;
        this.confirmPassword = confirmPassword;
    }
}

package com.furniture.miley.exception.customexception;

import lombok.Getter;

public class UnavailableUserException extends Exception {
    @Getter
    private String username;
    public UnavailableUserException(String message, String username) {
        super(message);
        this.username = username;
    }
}

package com.furniture.miley.exception.customexception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends Exception {

    @Getter
    private HttpStatus status;
    public ResourceNotFoundException(String message) {
        super(message);
        this.status = HttpStatus.NOT_FOUND;
    }
}

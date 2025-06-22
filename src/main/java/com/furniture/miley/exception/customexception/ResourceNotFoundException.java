package com.furniture.miley.exception.customexception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends Exception {

    @Getter
    private String resourceName;

    @Getter
    private HttpStatus status;

    public ResourceNotFoundException(String message) {
        super(message);
        this.status = HttpStatus.NOT_FOUND;
    }

    public ResourceNotFoundException(String message, String resourceName) {
        super(message);
        this.status = HttpStatus.NOT_FOUND;
        this.resourceName = resourceName;
    }
}

package com.furniture.miley.exception.customexception;

public class PrevStatusRequiredException extends RuntimeException {
    private String statusRequired;
    public PrevStatusRequiredException(String message, String statusRequired) {
        super(message);
        this.statusRequired = statusRequired;
    }
}

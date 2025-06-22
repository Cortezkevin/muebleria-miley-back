package com.furniture.miley.exception.customexception;

public class NotBelongCartException extends RuntimeException {
    public NotBelongCartException(String message) {
        super(message);
    }
}

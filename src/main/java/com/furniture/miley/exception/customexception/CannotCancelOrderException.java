package com.furniture.miley.exception.customexception;

public class CannotCancelOrderException extends Exception {
    public CannotCancelOrderException(String message) {
        super(message);
    }
}

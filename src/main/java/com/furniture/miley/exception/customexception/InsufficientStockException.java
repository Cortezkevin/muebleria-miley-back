package com.furniture.miley.exception.customexception;

public class InsufficientStockException extends Exception {
    private String resource;

    public InsufficientStockException(String message, String resource) {
        super(message);
        this.resource = resource;
    }
}

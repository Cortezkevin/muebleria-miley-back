package com.furniture.miley.exception.customexception;

public class FinishCurrentProcessException extends Exception {
    private String process;

    public FinishCurrentProcessException(String message, String process) {
        super(message);
        this.process = process;
    }
}

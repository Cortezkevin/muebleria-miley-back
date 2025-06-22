package com.furniture.miley.exception.customexception;

public class AlreadyStartedProcessException extends Exception {
    private String process;
    public AlreadyStartedProcessException(String message, String process) {
        super(message);
        this.process = process;
    }
}

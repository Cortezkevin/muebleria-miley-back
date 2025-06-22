package com.furniture.miley.exception.customexception;

import lombok.Getter;

public class AbortedProcessException extends Exception {
    @Getter
    private String process;
    public AbortedProcessException(String message, String process) {
        super(message);
        this.process = process;
    }
}

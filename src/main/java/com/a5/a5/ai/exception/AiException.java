package com.a5.a5.ai.exception;

import org.springframework.http.HttpStatus;

public class AiException extends RuntimeException {
    private final HttpStatus status;

    public AiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
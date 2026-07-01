package com.a5.a5.ai.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AiException.class)
    public ResponseEntity<Map<String, String>> handleAiException(AiException e) {
        return ResponseEntity
                .status(e.getStatus())
                .body(Map.of("error", e.getMessage(), "code", e.getStatus().toString()));
    }
}
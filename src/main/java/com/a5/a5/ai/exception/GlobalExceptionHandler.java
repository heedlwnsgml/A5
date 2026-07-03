package com.a5.a5.ai.exception;

import com.a5.a5.ai.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

// 전역 예외 처리기 (기존 코드 병합)
@ControllerAdvice
public class GlobalExceptionHandler {

    // 기존 AiException 처리 (원본 유지)
    @ExceptionHandler(AiException.class)
    public ResponseEntity<Map<String, String>> handleAiException(AiException e) {
        return ResponseEntity
                .status(e.getStatus())
                .body(Map.of("error", e.getMessage(), "code", e.getStatus().toString()));
    }

    // 신규 CustomApiException 처리 추가
    @ExceptionHandler(CustomApiException.class)
    public ResponseEntity<ErrorResponse> handleCustomApiException(CustomApiException e) {
        ErrorResponse response = new ErrorResponse(e.getHttpStatus().value(), e.getMessage());
        return new ResponseEntity<>(response, e.getHttpStatus());
    }

    // 신규 일반 Exception 처리 추가
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "서버 내부 오류가 발생했습니다."
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
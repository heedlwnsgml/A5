package com.a5.a5.ai.exception;

import com.a5.a5.ai.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// REST API 전용 전역 예외 처리기
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 기존 AiException 처리 (ErrorResponse 규격으로 응답 통일)
    @ExceptionHandler(AiException.class)
    public ResponseEntity<ErrorResponse> handleAiException(AiException e) {
        ErrorResponse response = new ErrorResponse(e.getStatus().value(), e.getMessage());
        return new ResponseEntity<>(response, e.getStatus());
    }

    // 신규 CustomApiException 처리
    @ExceptionHandler(CustomApiException.class)
    public ResponseEntity<ErrorResponse> handleCustomApiException(CustomApiException e) {
        ErrorResponse response = new ErrorResponse(e.getHttpStatus().value(), e.getMessage());
        return new ResponseEntity<>(response, e.getHttpStatus());
    }

    // DTO 파라미터 검증 실패 예외 처리 추가 (@Valid 방어용)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 일반 Exception 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "서버 내부 오류가 발생했습니다."
        );
        e.printStackTrace();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
package com.a5.a5.ai.exception;

import org.springframework.http.HttpStatus;

// 커스텀 API 예외 처리
public class CustomApiException extends RuntimeException {
    private final HttpStatus httpStatus;

    // 예외 초기화
    public CustomApiException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    // HTTP 상태 반환
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
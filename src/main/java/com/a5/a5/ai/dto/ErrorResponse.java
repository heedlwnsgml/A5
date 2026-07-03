package com.a5.a5.ai.dto;

// 에러 응답 DTO
public class ErrorResponse {
    private int status;
    private String message;

    // 생성자
    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    // 상태 코드 반환
    public int getStatus() {
        return status;
    }

    // 에러 메시지 반환
    public String getMessage() {
        return message;
    }
}
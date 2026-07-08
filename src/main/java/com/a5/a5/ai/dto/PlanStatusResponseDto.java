package com.a5.a5.ai.dto;

import lombok.Data;

@Data
public class PlanStatusResponseDto {
    private Long planId;      // DB에 생성된 임시 일정 ID
    private String status;    // PENDING, COMPLETED, FAILED
    private String message;   // 에러 발생 시 안내 메시지

    // 완료(COMPLETED) 상태일 때만 데이터가 채워집니다.
    private AiRouteResponseDto result;
}
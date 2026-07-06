package com.a5.a5.ai.dto;

import lombok.Data;

//개별 구간 요금 및 상세 정보를 담는 DTO
@Data
public class RouteSegmentDto {
    private String lineName;    // 탑승 노선명
    private String operator;    // 운영사
    private int timeMinutes;    // 소요 시간(분)
    private int segmentFare;    // 구간 요금(엔)
}
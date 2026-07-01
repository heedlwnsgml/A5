package com.a5.a5.ai.dto;

import lombok.Data;

@Data // Getter, Setter를 자동으로 만들어주는
public class TravelRequestDto {
    private String destination; // 여행지 (예: 전주, 제주도)
    private int durationDays;   // 여행 일수 (예: 3)
    private String theme;       // 여행 테마 (예: 식도락, 힐링)
    private int companions;     // 동행 인원 수
    //추후 추가
}
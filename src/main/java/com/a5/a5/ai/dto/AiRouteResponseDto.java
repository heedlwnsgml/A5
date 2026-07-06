package com.a5.a5.ai.dto;

import lombok.Data;
import java.util.List;

@Data
public class AiRouteResponseDto {
    // 간결한 주석: AI 추천 사유 및 전체 일정 요약
    private String reason;

    // 간결한 주석: 타임라인 상세 리스트
    private List<TimelineItem> timeline;

    // 간결한 주석: 최적화된 교통 요금 및 패스 정보 포함
    private RouteInfoDto transportOptimization;

    @Data
    public static class TimelineItem {
        private int day;
        private String time;
        private String placeName;
        private String category;
        private String description;
        private Double latitude;
        private Double longitude;
    }
}
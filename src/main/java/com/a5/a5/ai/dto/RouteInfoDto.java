package com.a5.a5.ai.dto;

import java.util.List;
import java.util.Set;

public class RouteInfoDto {
    // 간결한 주석: 총 소요 시간 및 전체 합산 요금
    private int totalTime;
    private int ticketFare;
    private int icCardFare;

    // 간결한 주석: 구간별 개별 요금 리스트 추가
    private List<RouteSegmentDto> segments;
    private Set<String> operators;
    private List<String> pathDetails;

    private int optimalFare;
    private String recommendedPassName;
    private String optimizationMessage;

    public RouteInfoDto() {}

    // 간결한 주석: Getter 및 Setter
    public int getTotalTime() { return totalTime; }
    public void setTotalTime(int totalTime) { this.totalTime = totalTime; }

    public int getTicketFare() { return ticketFare; }
    public void setTicketFare(int ticketFare) { this.ticketFare = ticketFare; }

    public int getIcCardFare() { return icCardFare; }
    public void setIcCardFare(int icCardFare) { this.icCardFare = icCardFare; }

    public List<RouteSegmentDto> getSegments() { return segments; }
    public void setSegments(List<RouteSegmentDto> segments) { this.segments = segments; }

    public Set<String> getOperators() { return operators; }
    public void setOperators(Set<String> operators) { this.operators = operators; }

    public List<String> getPathDetails() { return pathDetails; }
    public void setPathDetails(List<String> pathDetails) { this.pathDetails = pathDetails; }

    public int getOptimalFare() { return optimalFare; }
    public void setOptimalFare(int optimalFare) { this.optimalFare = optimalFare; }

    public String getRecommendedPassName() { return recommendedPassName; }
    public void setRecommendedPassName(String recommendedPassName) { this.recommendedPassName = recommendedPassName; }

    public String getOptimizationMessage() { return optimizationMessage; }
    public void setOptimizationMessage(String optimizationMessage) { this.optimizationMessage = optimizationMessage; }
}
package com.a5.a5.ai.dto;

import java.util.List;
import java.util.Set;

public class RouteInfoDto {
    // 간결한 주석: 소요 시간 및 기본 요금
    private int totalTime;
    private int ticketFare;
    private int icCardFare;

    // 간결한 주석: 경로 상의 모든 운영 철도회사/노선명 집합
    private Set<String> operators;
    private List<String> pathDetails;

    // 간결한 주석: 패스권 비교 후 도출된 최종 최적 요금 및 추천 메시지
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
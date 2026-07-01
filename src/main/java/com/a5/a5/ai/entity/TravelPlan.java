package com.a5.a5.ai.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "travel_plan")
@Getter
@Setter
public class TravelPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String destination;

    // AI가 생성한 긴 내용을 담기 위해 TEXT 타입으로 설정
    @Column(name = "plan_content", columnDefinition = "TEXT", nullable = false)
    private String planContent;

    // 데이터 생성 시간 필드 추가
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    // 엔티티가 저장되기 직전에 현재 시간 설정
    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
    }
}
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

    // AI 결과값이 없을 수 있으므로 nullable 허용으로 변경
    @Column(name = "plan_content", columnDefinition = "TEXT")
    private String planContent;

    // 진행 상태 관리를 위한 필드 추가
    @Column(nullable = false)
    private String status;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
    }
}
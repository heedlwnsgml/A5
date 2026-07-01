package com.a5.a5.ai.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class TravelPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String destination; // 여행지
    private String planContent; // AI가 생성한 여행 계획 내용
}
package com.a5.a5.ai.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class CachedPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 검색어(장소명)를 기준으로 캐시 조회
    @Column(nullable = false, unique = true)
    private String placeName;

    private String address;
    private double lat;
    private double lng;

    // 데이터 갱신 시간
    private LocalDateTime updatedAt;

    // 저장 및 업데이트 시 시간 자동 기록
    @PrePersist
    @PreUpdate
    public void prePersist() {
        this.updatedAt = LocalDateTime.now();
    }
}
package com.a5.a5.ai.repository;

import com.a5.a5.ai.entity.TravelPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TravelPlanRepository extends JpaRepository<TravelPlan, Long> {
    // 가장 먼저 조회되는 데이터를 가져옵니다 (정렬 조건 제거로 에러 해결)
    Optional<TravelPlan> findFirstByDestination(String destination);
}
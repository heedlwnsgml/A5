package com.a5.a5.ai.repository;

import com.a5.a5.ai.entity.TravelPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelPlanRepository extends JpaRepository<TravelPlan, Long> {
    // JpaRepository를 상속받는 것만으로도 기본적인 저장(save), 조회(findById) 기능이 자동으로 완성됩니다.
}
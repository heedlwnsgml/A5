package com.a5.a5.ai.repository;

import com.a5.a5.ai.entity.TravelPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TravelPlanRepository extends JpaRepository<TravelPlan, Long> {

    // 가장 먼저 조회되는 데이터를 가져옴
    Optional<TravelPlan> findFirstByDestination(String destination);

    // 24시간이 지났으며 상태가 PENDING 또는 FAILED인 데이터를 일괄 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM TravelPlan t WHERE t.status IN ('PENDING', 'FAILED') AND t.createdDate <= :thresholdDate")
    void deleteOldTemporaryPlans(@Param("thresholdDate") LocalDateTime thresholdDate);
}
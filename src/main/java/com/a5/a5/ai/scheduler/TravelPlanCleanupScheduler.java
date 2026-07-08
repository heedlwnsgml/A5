package com.a5.a5.ai.scheduler;

import com.a5.a5.ai.repository.TravelPlanRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TravelPlanCleanupScheduler {

    private final TravelPlanRepository travelPlanRepository;

    public TravelPlanCleanupScheduler(TravelPlanRepository travelPlanRepository) {
        this.travelPlanRepository = travelPlanRepository;
    }

    // 한국 시간 기준 매일 새벽 3시에 자동 실행
    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
    public void cleanupOldTemporaryPlans() {
        // 현재 시간으로부터 24시간 전의 시간을 임계점으로 설정
        LocalDateTime thresholdDate = LocalDateTime.now().minusHours(24);

        travelPlanRepository.deleteOldTemporaryPlans(thresholdDate);
        System.out.println("24시간이 지난 임시/실패 여행 일정 데이터 자동 정리가 완료되었습니다. 기준 시간: " + thresholdDate);
    }
}
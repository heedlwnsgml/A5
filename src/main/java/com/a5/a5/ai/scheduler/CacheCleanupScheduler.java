package com.a5.a5.ai.scheduler;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CacheCleanupScheduler {

    // 매주 월요일 새벽 4시에 구글 장소 좌표 캐시 일괄 삭제
    @Scheduled(cron = "0 0 4 * * MON", zone = "Asia/Seoul")
    @CacheEvict(value = "placeCoordinates", allEntries = true)
    public void clearPlaceCoordinatesCache() {
        System.out.println("구글 장소 좌표 로컬 캐시가 정기 초기화되었습니다.");
    }
}
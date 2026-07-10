package com.a5.a5.ai.controller;

import com.a5.a5.ai.dto.PlanStatusResponseDto;
import com.a5.a5.ai.dto.TravelRequestDto;
import com.a5.a5.ai.service.AiTravelService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/ai")
public class AiTravelController {

    private final AiTravelService aiTravelService;
    private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();

    public AiTravelController(AiTravelService aiTravelService) {
        this.aiTravelService = aiTravelService;
    }

    // 클라이언트 IP별로 1분에 최대 3번까지만 호출 가능하도록 Bucket 생성
    private Bucket resolveBucket(String ip) {
        return bucketCache.computeIfAbsent(ip, this::newBucket);
    }

    private Bucket newBucket(String ip) {
        Bandwidth limit = Bandwidth.classic(3, Refill.intervally(3, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    // 일정 생성 요청 수신 엔드포인트
    @PostMapping("/plan")
    public ResponseEntity<?> requestTravelPlan(@Valid @RequestBody TravelRequestDto request, HttpServletRequest httpRequest) {
        String clientIp = httpRequest.getRemoteAddr();
        Bucket bucket = resolveBucket(clientIp);

        // 호출 횟수를 초과했을 경우 차단 (429 에러 반환)
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "1분에 최대 3번까지만 요청할 수 있습니다. 잠시 후 다시 시도해주세요."));
        }

        // DB에 PENDING 상태로 만들고 일정 ID 획득
        Long planId = aiTravelService.requestTravelPlan(request);

        // 무거운 통신 작업은 백그라운드 스레드로 던짐 (컨트롤러는 기다리지 않음)
        aiTravelService.processTravelPlanInBackground(planId, request);

        // 프론트엔드에게 즉시 응답 반환
        return ResponseEntity.ok(Map.of(
                "planId", planId,
                "message", "일정 생성이 시작되었습니다. status API를 통해 상태를 확인하세요."
        ));
    }

    // 프론트엔드가 진행 상태를 확인하기 위해 주기적으로 찌르는 폴링 엔드포인트
    @GetMapping("/plan/{planId}/status")
    public ResponseEntity<PlanStatusResponseDto> getPlanStatus(@PathVariable Long planId) {
        PlanStatusResponseDto status = aiTravelService.checkPlanStatus(planId);
        return ResponseEntity.ok(status);
    }
}
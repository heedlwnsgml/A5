package com.a5.a5.ai.controller;

import com.a5.a5.ai.dto.TravelRequestDto;
import com.a5.a5.ai.service.AiTravelService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiTravelController {

    private final AiTravelService aiTravelService;

    public AiTravelController(AiTravelService aiTravelService) {
        this.aiTravelService = aiTravelService;
    }

    // 앱에서 여행 계획을 요청받는 주소: /api/ai/plan
    @PostMapping("/plan")
    public String getTravelPlan(@RequestBody TravelRequestDto request) {
        // 서비스의 변경된 메서드 이름으로 호출
        return aiTravelService.generateTravelPlan(request);
    }
}
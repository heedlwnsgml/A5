package com.a5.a5.ai.controller;

import com.a5.a5.ai.dto.TravelRequestDto;
import com.a5.a5.ai.dto.AiRouteResponseDto;
import com.a5.a5.ai.service.AiTravelService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiTravelController {

    private final AiTravelService aiTravelService;

    public AiTravelController(AiTravelService aiTravelService) {
        this.aiTravelService = aiTravelService;
    }

    // 앱에서 여행 계획 요청 처리 (유효성 검사 포함)
    @PostMapping("/plan")
    public ResponseEntity<AiRouteResponseDto> getTravelPlan(@Valid @RequestBody TravelRequestDto request) {
        AiRouteResponseDto response = aiTravelService.generateTravelPlan(request);
        return ResponseEntity.ok(response);
    }
}
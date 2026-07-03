package com.a5.a5.ai.controller;

import com.a5.a5.ai.dto.RouteInfoDto;
import com.a5.a5.ai.service.RouteOptimizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/route")
public class RouteController {

    private final RouteOptimizationService routeOptimizationService;

    // 간결한 주석: 의존성 주입
    public RouteController(RouteOptimizationService routeOptimizationService) {
        this.routeOptimizationService = routeOptimizationService;
    }

    // 간결한 주석: 최적 경로 및 요금 조회 API
    @GetMapping("/optimize")
    public ResponseEntity<RouteInfoDto> getOptimizedRoute(
            @RequestParam double startLat,
            @RequestParam double startLng,
            @RequestParam double goalLat,
            @RequestParam double goalLng,
            @RequestParam String cityName) {

        RouteInfoDto result = routeOptimizationService.getOptimizedRoute(
                startLat, startLng, goalLat, goalLng, cityName
        );

        if (result == null) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(result);
    }
}
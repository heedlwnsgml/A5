package com.a5.a5.ai.controller;

import com.a5.a5.ai.dto.PlaceLocationDto;
import com.a5.a5.ai.dto.RouteInfoDto;
import com.a5.a5.ai.service.GooglePlaceService;
import com.a5.a5.ai.service.NavitimeRouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class PlaceTestController {

    private final GooglePlaceService googlePlaceService;
    private final NavitimeRouteService navitimeRouteService;

    // NavitimeRouteService로 의존성 주입 변경
    public PlaceTestController(GooglePlaceService googlePlaceService, NavitimeRouteService navitimeRouteService) {
        this.googlePlaceService = googlePlaceService;
        this.navitimeRouteService = navitimeRouteService;
    }

    // 장소 검색 테스트
    @GetMapping("/place")
    public ResponseEntity<PlaceLocationDto> testPlaceSearch(@RequestParam String name) {
        PlaceLocationDto result = googlePlaceService.findPlaceLocation(name);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);
    }

    // 경로 탐색 테스트
    @GetMapping("/route")
    public ResponseEntity<RouteInfoDto> testRoute() {
        // 도쿄역 좌표
        double originLat = 35.6812;
        double originLng = 139.7671;

        // 신주쿠역 좌표
        double destLat = 35.6896;
        double destLng = 139.7005;

        // 5번째 "TRANSIT" 파라미터 제거, 4개만 전달
        RouteInfoDto result = navitimeRouteService.getRouteInfo(originLat, originLng, destLat, destLng);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);
    }
}
package com.a5.a5.ai.service;

import com.a5.a5.ai.dto.RouteInfoDto;
import com.a5.a5.ai.entity.PassCoverage;
import com.a5.a5.ai.entity.TourPass;
import com.a5.a5.ai.repository.TourPassRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class RouteOptimizationService {

    private final NavitimeRouteService navitimeRouteService;
    private final TourPassRepository tourPassRepository;

    public RouteOptimizationService(NavitimeRouteService navitimeRouteService, TourPassRepository tourPassRepository) {
        this.navitimeRouteService = navitimeRouteService;
        this.tourPassRepository = tourPassRepository;
    }

    public RouteInfoDto getOptimizedRoute(double startLat, double startLng, double goalLat, double goalLng, String cityName) {
        RouteInfoDto routeInfo = navitimeRouteService.getRouteInfo(startLat, startLng, goalLat, goalLng);

        if (routeInfo == null) {
            return null;
        }

        // IC 카드 요금이 0엔(제공 안 됨)일 경우 일반 표 요금을 기본 최적 요금으로 설정
        int defaultOptimalFare;
        if (routeInfo.getIcCardFare() > 0) {
            defaultOptimalFare = Math.min(routeInfo.getTicketFare(), routeInfo.getIcCardFare());
        } else {
            defaultOptimalFare = routeInfo.getTicketFare();
        }

        routeInfo.setOptimalFare(defaultOptimalFare);
        routeInfo.setOptimizationMessage("일반 표 또는 IC 카드를 이용하는 것이 가장 저렴합니다.");

        List<TourPass> availablePasses = tourPassRepository.findByCityNameWithCoverages(cityName);
        Set<String> routeOperators = routeInfo.getOperators();

        // 노선 정보가 비어있다면 패스권 비교를 수행할 수 없으므로 바로 반환
        if (routeOperators.isEmpty()) {
            return routeInfo;
        }

        for (TourPass pass : availablePasses) {
            boolean canUsePass = true;

            for (String operator : routeOperators) {
                boolean isCovered = pass.getCoverages().stream()
                        .map(PassCoverage::getOperatorName)
                        .anyMatch(operator::contains);

                if (!isCovered) {
                    canUsePass = false;
                    break;
                }
            }

            if (canUsePass && pass.getPrice() < routeInfo.getOptimalFare()) {
                routeInfo.setOptimalFare(pass.getPrice());
                routeInfo.setRecommendedPassName(pass.getPassName());

                int savedAmount = defaultOptimalFare - pass.getPrice();
                routeInfo.setOptimizationMessage(
                        String.format("'%s'를 구매하시면 일반 요금 대비 %d엔 절약됩니다.", pass.getPassName(), savedAmount)
                );
            }
        }

        return routeInfo;
    }
}
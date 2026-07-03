package com.a5.a5.ai.service;

import com.a5.a5.ai.dto.RouteInfoDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Service
public class GoogleRouteService {

    @Value("${google.maps.key}")
    private String googleMapsKey;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    // RestClient, ObjectMapper 초기화
    public GoogleRouteService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
        this.objectMapper = new ObjectMapper();
    }

    // Directions API를 활용한 경로 정보 반환
    public RouteInfoDto getRouteInfo(double originLat, double originLng, double destLat, double destLng, String travelMode) {
        // 구글 Directions API 엔드포인트
        String url = "https://maps.googleapis.com/maps/api/directions/json";

        // Directions API 규격에 맞게 이동 수단 문자열 변환
        String mode = travelMode.toLowerCase();
        if (mode.equals("drive")) mode = "driving";
        if (mode.equals("walk")) mode = "walking";

        // GET 요청 URL 조합
        String requestUrl = url + "?origin=" + originLat + "," + originLng +
                "&destination=" + destLat + "," + destLng +
                "&mode=" + mode +
                "&key=" + googleMapsKey;

        // 대중교통 이용 시 실시간 배차 조회를 위해 departure_time=now 필수 추가
        if ("transit".equals(mode)) {
            requestUrl += "&departure_time=now";
        }

        try {
            // API GET 호출
            String response = restClient.get()
                    .uri(requestUrl)
                    .retrieve()
                    .body(String.class);

            JsonNode rootNode = objectMapper.readTree(response);
            String status = rootNode.path("status").asText();

            System.out.println("Directions API 응답 상태: " + status);

            // 정상적으로 경로를 찾은 경우
            if ("OK".equals(status)) {
                JsonNode firstRoute = rootNode.path("routes").get(0);
                JsonNode firstLeg = firstRoute.path("legs").get(0);

                RouteInfoDto dto = new RouteInfoDto();

                // Directions API의 JSON 구조(legs 배열 내부)에서 거리와 시간 추출
                dto.setDistanceMeters(firstLeg.path("distance").path("value").asInt());
                // Routes API와 동일한 포맷("초s")을 유지하기 위해 's' 문자열 추가
                dto.setDuration(firstLeg.path("duration").path("value").asText() + "s");

                return dto;
            } else {
                // 구글이 경로를 찾지 못했거나 권한 문제 발생 시 로그 출력
                System.err.println("경로 검색 실패. 구글 API 상태 코드: " + status);
                System.err.println("응답 상세 내용: " + response);
            }

        } catch (HttpClientErrorException e) {
            // 4xx, 5xx HTTP 통신 에러 로깅
            System.err.println("Directions API HTTP 에러 상태 코드: " + e.getStatusCode());
            System.err.println("Directions API HTTP 에러 상세 내역: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            // 코드 내부 파싱 에러 등 알 수 없는 예외 로깅
            System.err.println("Directions API 처리 중 알 수 없는 예외 발생: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}
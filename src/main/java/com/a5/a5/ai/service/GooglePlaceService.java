package com.a5.a5.ai.service;

import com.a5.a5.ai.dto.PlaceLocationDto;
import com.a5.a5.ai.entity.CachedPlace;
import com.a5.a5.ai.repository.CachedPlaceRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class GooglePlaceService {

    @Value("${google.maps.key}")
    private String googleMapsKey;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final CachedPlaceRepository cachedPlaceRepository;

    // 캐시 유효 기간 (7일)
    private static final int CACHE_VALID_DAYS = 7;

    public GooglePlaceService(RestClient.Builder restClientBuilder,
                              CachedPlaceRepository cachedPlaceRepository) {
        this.restClient = restClientBuilder.build();
        this.objectMapper = new ObjectMapper();
        this.cachedPlaceRepository = cachedPlaceRepository;
    }

    public PlaceLocationDto findPlaceLocation(String placeName) {
        // 1. DB에서 캐시 데이터 조회
        Optional<CachedPlace> cachedOpt = cachedPlaceRepository.findByPlaceName(placeName);

        if (cachedOpt.isPresent()) {
            CachedPlace cached = cachedOpt.get();
            // 2. 유효 기간 검사 (현재 시간이 업데이트 시간 + 7일 이전인지 확인)
            if (cached.getUpdatedAt().isAfter(LocalDateTime.now().minusDays(CACHE_VALID_DAYS))) {
                System.out.println("DB 캐시 적중 (API 호출 생략): " + placeName);

                PlaceLocationDto dto = new PlaceLocationDto();
                dto.setName(cached.getPlaceName());
                dto.setAddress(cached.getAddress());
                dto.setLat(cached.getLat());
                dto.setLng(cached.getLng());

                return dto;
            } else {
                System.out.println("캐시 만료 (7일 경과), API 재호출 진행: " + placeName);
            }
        }

        // 3. 캐시가 없거나 만료된 경우 API 통신
        PlaceLocationDto apiResult = fetchFromGoogleApi(placeName);

        // 4. API 통신 성공 시 DB에 신규 저장 또는 기존 데이터 갱신
        if (apiResult != null) {
            CachedPlace placeToSave = cachedOpt.orElse(new CachedPlace());
            placeToSave.setPlaceName(placeName);
            placeToSave.setAddress(apiResult.getAddress());
            placeToSave.setLat(apiResult.getLat());
            placeToSave.setLng(apiResult.getLng());

            cachedPlaceRepository.save(placeToSave);
        }

        return apiResult;
    }

    // 실제 구글 API 호출 로직 분리
    private PlaceLocationDto fetchFromGoogleApi(String placeName) {
        String url = "https://places.googleapis.com/v1/places:searchText";
        Map<String, String> requestBody = Map.of("textQuery", placeName);

        try {
            String response = restClient.post()
                    .uri(url)
                    .header("X-Goog-Api-Key", googleMapsKey)
                    .header("X-Goog-FieldMask", "places.displayName,places.formattedAddress,places.location")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode places = rootNode.path("places");

            if (places.isArray() && places.size() > 0) {
                JsonNode firstPlace = places.get(0);
                PlaceLocationDto dto = new PlaceLocationDto();

                dto.setName(firstPlace.path("displayName").path("text").asText());
                dto.setAddress(firstPlace.path("formattedAddress").asText());

                JsonNode location = firstPlace.path("location");
                dto.setLat(location.path("latitude").asDouble());
                dto.setLng(location.path("longitude").asDouble());

                return dto;
            }
        } catch (Exception e) {
            System.err.println("Google Places API 호출 실패: " + e.getMessage());
        }

        return null;
    }
}
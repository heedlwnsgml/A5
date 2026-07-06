package com.a5.a5.ai.service;

import com.a5.a5.ai.dto.AiRouteResponseDto;
import com.a5.a5.ai.dto.PlaceLocationDto;
import com.a5.a5.ai.dto.RouteInfoDto;
import com.a5.a5.ai.dto.TravelRequestDto;
import com.a5.a5.ai.entity.TravelPlan;
import com.a5.a5.ai.exception.AiException;
import com.a5.a5.ai.repository.TravelPlanRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class AiTravelService {

    @Value("${ai.gemini.url}") private String geminiUrl;
    @Value("${ai.gemini.key}") private String geminiKey;
    @Value("${ai.chatgpt.url:}") private String chatgptUrl;

    private final RestClient restClient;
    private final TravelPlanRepository repository;
    private final EmailService emailService;
    private final GooglePlaceService googlePlaceService;
    private final RouteOptimizationService routeOptimizationService;
    private final ObjectMapper objectMapper;

    private int failureCount = 0;
    private boolean isSystemPaused = false;
    private static final int MAX_FAILURE_LIMIT = 5;
    private String activeModel = "GEMINI";

    // 생성자 의존성 주입
    public AiTravelService(RestClient.Builder restClientBuilder,
                           TravelPlanRepository repository,
                           EmailService emailService,
                           GooglePlaceService googlePlaceService,
                           RouteOptimizationService routeOptimizationService) {
        this.restClient = restClientBuilder.build();
        this.repository = repository;
        this.emailService = emailService;
        this.googlePlaceService = googlePlaceService;
        this.routeOptimizationService = routeOptimizationService;
        this.objectMapper = new ObjectMapper();
    }

    // 여행 계획 생성 통합 파이프라인 (트랜잭션 보장)
    @Transactional
    public AiRouteResponseDto generateTravelPlan(TravelRequestDto request) {
        if (isSystemPaused) {
            throw new AiException("시스템이 일시 정지되었습니다.", HttpStatus.SERVICE_UNAVAILABLE);
        }

        try {
            String aiResultString = "GEMINI".equals(activeModel) ? callGemini(request) : callChatGPT(request);
            AiRouteResponseDto responseDto = parseGeminiResponse(aiResultString);

            enrichWithLocationData(responseDto);
            enrichWithRouteOptimization(responseDto, request.getDestination());

            savePlanToDb(request.getDestination(), aiResultString);

            return responseDto;
        } catch (Exception e) {
            System.err.println(activeModel + " 호출 실패: " + e.getMessage());
            handleFailure();
            switchModel();
            throw new AiException("AI 생성 실패: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String callGemini(TravelRequestDto request) {
        // 프롬프트 제약 조건 설정 (JSON 출력 강제)
        String systemInstruction = "너는 여행 계획 전문가야. 답변할 때 다음 규칙을 반드시 지켜:\n" +
                "1. 이모지를 절대 사용하지 마.\n" +
                "2. 전문적이고 간결한 말투를 유지해.\n" +
                "3. 답변은 한국어로 작성해.\n" +
                "4. **을 절대 사용하지 마.\n" +
                "5. destination을 비슷하거나 추측되는 지역으로 변경하지 마.\n" +
                "6. destination이 주소를 가르키거나 확실한 지역명이 아니라면 재입력 요청을 해.\n" +
                "7. destination이 도로명 주소로 입력될 경우에는 지번 주소로 변경해.\n" +
                "8. 응답은 반드시 아래의 JSON 형식으로만 출력해. 마크다운 기호(```json)는 절대 넣지 마.\n" +
                "{\n" +
                "  \"reason\": \"전체 일정 요약 및 사유\",\n" +
                "  \"timeline\": [\n" +
                "    { \"day\": 1, \"time\": \"09:00\", \"placeName\": \"장소명\", \"category\": \"분류\", \"description\": \"설명\" }\n" +
                "  ]\n" +
                "}";

        String prompt = String.format("%s으로 %d일간 %s 여행 계획을 짜줘.",
                request.getDestination(), request.getDurationDays(), request.getTheme());

        String combinedPrompt = systemInstruction + "\n\n질문: " + prompt;

        Map<String, Object> body = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of("text", combinedPrompt)
                        })
                }
        );

        return restClient.post()
                .uri(geminiUrl + "?key=" + geminiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);
    }

    private String callChatGPT(TravelRequestDto request) {
        throw new RuntimeException("ChatGPT API 장애 발생(테스트)");
    }

    private void savePlanToDb(String destination, String content) {
        TravelPlan travelPlan = new TravelPlan();
        travelPlan.setDestination(destination);
        travelPlan.setPlanContent(content);
        repository.save(travelPlan);
    }

    private void handleFailure() {
        failureCount++;
        System.err.println("현재 누적 장애 횟수: " + failureCount);
        if (failureCount >= MAX_FAILURE_LIMIT) {
            isSystemPaused = true;
            sendAlertToAdmin();
        }
    }

    private void switchModel() {
        this.activeModel = "GEMINI".equals(activeModel) ? "CHATGPT" : "GEMINI";
        System.out.println("모델 전환: 현재 활성 모델 -> " + activeModel);
    }

    private void sendAlertToAdmin() {
        System.err.println("!!! 5회 장애 누적: 시스템 정지 및 관리자 알림 발송 !!!");
        emailService.sendAdminAlert("장애 발생 횟수 5회 초과로 인한 서비스 일시 중단.");
    }

    public void resetSystem() {
        this.isSystemPaused = false;
        this.failureCount = 0;
        this.activeModel = "GEMINI";
        System.out.println("관리자에 의해 시스템이 정상화되었습니다.");
    }

    // AI 텍스트를 DTO 객체로 파싱
    private AiRouteResponseDto parseGeminiResponse(String responseBody) throws Exception {
        JsonNode rootNode = objectMapper.readTree(responseBody);
        String aiText = rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
        aiText = aiText.replace("```json", "").replace("```", "").trim();
        return objectMapper.readValue(aiText, AiRouteResponseDto.class);
    }

    // 타임라인 장소의 위경도 주입
    private void enrichWithLocationData(AiRouteResponseDto responseDto) {
        if (responseDto.getTimeline() == null) return;

        for (AiRouteResponseDto.TimelineItem item : responseDto.getTimeline()) {
            PlaceLocationDto locationDto = googlePlaceService.findPlaceLocation(item.getPlaceName());
            if (locationDto != null) {
                item.setLatitude(locationDto.getLat());
                item.setLongitude(locationDto.getLng());
            }
        }
    }

    // 경로 및 패스 요금 최적화 데이터 주입
    private void enrichWithRouteOptimization(AiRouteResponseDto responseDto, String cityName) {
        if (responseDto.getTimeline() == null || responseDto.getTimeline().size() < 2) return;

        AiRouteResponseDto.TimelineItem firstItem = responseDto.getTimeline().get(0);
        AiRouteResponseDto.TimelineItem lastItem = responseDto.getTimeline().get(responseDto.getTimeline().size() - 1);

        if (firstItem.getLatitude() != null && lastItem.getLatitude() != null) {
            RouteInfoDto routeInfo = routeOptimizationService.getOptimizedRoute(
                    firstItem.getLatitude(), firstItem.getLongitude(),
                    lastItem.getLatitude(), lastItem.getLongitude(),
                    cityName
            );
            responseDto.setTransportOptimization(routeInfo);
        }
    }
}
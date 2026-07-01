package com.a5.a5.ai.service;

import com.a5.a5.ai.dto.TravelRequestDto;
import com.a5.a5.ai.entity.TravelPlan;
import com.a5.a5.ai.exception.AiException;
import com.a5.a5.ai.repository.TravelPlanRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
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

    private int failureCount = 0;
    private boolean isSystemPaused = false;
    private static final int MAX_FAILURE_LIMIT = 5;
    private String activeModel = "GEMINI";

    public AiTravelService(RestClient.Builder restClientBuilder,
                           TravelPlanRepository repository,
                           EmailService emailService) {
        this.restClient = restClientBuilder.build();
        this.repository = repository;
        this.emailService = emailService;
    }

    public String generateTravelPlan(TravelRequestDto request) {
        if (isSystemPaused) {
            throw new AiException("시스템이 일시 정지되었습니다.", HttpStatus.SERVICE_UNAVAILABLE);
        }

        try {
            // 1. AI API 호출 및 생성 시도
            String result = "GEMINI".equals(activeModel) ? callGemini(request) : callChatGPT(request);

            // 2. 생성 성공 시 DB 저장
            savePlanToDb(request.getDestination(), result);
            return result;
        } catch (Exception e) {
            System.err.println(activeModel + " 호출 실패, DB 조회 시도: " + e.getMessage());

            // 3. 실패 시 DB에서 폴백 데이터 조회
            return repository.findFirstByDestination(request.getDestination())
                    .map(TravelPlan::getPlanContent)
                    .orElseThrow(() -> {
                        // 4. DB에도 없으면 장애 처리 및 모델 전환
                        handleFailure();
                        switchModel();
                        return new AiException("AI 생성 실패 및 DB 데이터 없음: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                    });
        }
    }

    private String callGemini(TravelRequestDto request) {
        /* [실제 운영 로직] */
        String prompt = String.format("%s으로 %d일간 %s 여행을 계획해줘.", request.getDestination(), request.getDurationDays(), request.getTheme());

        // Gemini API v1beta 요청 구조
        Map<String, Object> body = Map.of(
            "contents", new Object[]{
                Map.of("parts", new Object[]{
                    Map.of("text", prompt)
                })
            }
        );

        return restClient.post()
                .uri(geminiUrl + "?key=" + geminiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);

        /* [테스트용 코드 - 주석 해제 시 사용] */
        // throw new RuntimeException("Gemini API 장애 발생(테스트)");
    }

    private String callChatGPT(TravelRequestDto request) {
        /* [실제 운영 로직]
        return restClient.post().uri(chatgptUrl).body(request).retrieve().body(String.class);

       [테스트용 코드 - 주석 해제 시 사용] */
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
}
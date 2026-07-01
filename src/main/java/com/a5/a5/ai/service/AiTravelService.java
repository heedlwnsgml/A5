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

    @Value("${ai.gemini.url}")
    private String geminiUrl;
    @Value("${ai.gemini.key}")
    private String geminiKey;
    @Value("${ai.chatgpt.url:}")
    private String chatgptUrl;

    private final RestClient restClient;
    private final TravelPlanRepository repository;
    private final EmailService emailService;

    private int failureCount = 0;
    private boolean isSystemPaused = false;
    private static final int MAX_FAILURE_LIMIT = 5;
    private String activeModel = "GEMINI"; // 현재 활성 모델 기억

    public AiTravelService(RestClient.Builder restClientBuilder,
                           TravelPlanRepository repository,
                           EmailService emailService) {
        this.restClient = restClientBuilder.build();
        this.repository = repository;
        this.emailService = emailService;
    }

    public String generateTravelPlanWithGemini(TravelRequestDto request) {
        if (isSystemPaused) {
            throw new AiException("시스템이 일시 정지되었습니다.", HttpStatus.SERVICE_UNAVAILABLE);
        }

        try {
            // 현재 모델에 따라 호출
            return "GEMINI".equals(activeModel) ? callGemini(request) : callChatGPT(request);
        } catch (Exception e) {
            System.err.println(activeModel + " 호출 실패: " + e.getMessage());
            handleFailure();
            switchModel(); // 실패 시 모델 교체
            throw new AiException("AI 모델 오류, 모델을 전환합니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void switchModel() {
        this.activeModel = "GEMINI".equals(activeModel) ? "CHATGPT" : "GEMINI";
        System.out.println("모델 전환: 현재 활성 모델 -> " + activeModel);
    }

    public void resetSystem() {
        this.isSystemPaused = false;
        this.failureCount = 0;
        this.activeModel = "GEMINI";
        System.out.println("관리자에 의해 시스템이 정상화되었습니다.");
    }

    private String callGemini(TravelRequestDto request) {
        /* [실제 운영 로직]
        String prompt = String.format("%s으로 %d일간 %s 여행을 계획해줘.", request.getDestination(), request.getDurationDays(), request.getTheme());
        Map<String, Object> body = Map.of("contents", new Object[]{Map.of("parts", new Object[]{Map.of("text", prompt)})});
        String response = restClient.post().uri(geminiUrl + "?key=" + geminiKey).contentType(MediaType.APPLICATION_JSON).body(body).retrieve().body(String.class);
        savePlanToDb(request.getDestination(), response);
        return response;
        */
        throw new RuntimeException("Gemini API 장애 발생(테스트)");
    }

    private String callChatGPT(TravelRequestDto request) {
        /* [실제 운영 로직]
        String response = restClient.post().uri(chatgptUrl).body(request).retrieve().body(String.class);
        savePlanToDb(request.getDestination(), response);
        return response;
        */
        throw new RuntimeException("ChatGPT API 장애 발생(테스트)");
    }

    private void handleFailure() {
        failureCount++;
        System.err.println("현재 누적 장애 횟수: " + failureCount);
        if (failureCount >= MAX_FAILURE_LIMIT) {
            isSystemPaused = true;
            sendAlertToAdmin();
        }
    }

    private void sendAlertToAdmin() {
        System.err.println("!!! 5회 장애 누적: 시스템 정지 및 관리자 알림 발송 !!!");
        emailService.sendAdminAlert("장애 발생 횟수 5회 초과로 인한 서비스 일시 중단.");
    }

    private void savePlanToDb(String destination, String content) {
        TravelPlan travelPlan = new TravelPlan();
        travelPlan.setDestination(destination);
        travelPlan.setPlanContent(content);
        repository.save(travelPlan);
    }
}
package com.a5.a5.ai.controller;

import com.a5.a5.ai.service.AiTravelService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AiTravelService aiTravelService;

    public AdminController(AiTravelService aiTravelService) {
        this.aiTravelService = aiTravelService;
    }

    @PostMapping("/reset")
    public String resetSystem() {
        aiTravelService.resetSystem();
        return "시스템이 정상화되었습니다.";
    }
}
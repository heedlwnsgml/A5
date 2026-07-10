package com.a5.a5.ai.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI a5ProjectApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("A5 AI Travel Planner API 명세서")
                        .description("CROSS(A5) 프로젝트 프론트엔드 연동을 위한 REST API 테스트 및 명세 페이지입니다.")
                        .version("v1.0.0"));
    }
}
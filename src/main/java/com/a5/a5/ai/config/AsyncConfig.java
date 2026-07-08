package com.a5.a5.ai.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {
    // 간결한 주석: Spring 부트의 비동기 처리(백그라운드 스레드)를 활성화합니다.
}
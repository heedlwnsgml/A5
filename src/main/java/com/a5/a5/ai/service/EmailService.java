package com.a5.a5.ai.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAdminAlert(String message) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo("vhghen7@gmail.com"); // 본인 메일로 테스트
            mail.setSubject("경고: AI 서비스 장애 발생");
            mail.setText(message);
            mailSender.send(mail);
            // 이 로그가 찍히면 스프링부트는 정상적으로 메일을 보낸 겁니다!
            System.err.println(">>>>> 관리자 메일 발송 성공!");
        } catch (Exception e) {
            System.err.println(">>>>> 메일 발송 에러 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
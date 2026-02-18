package com.navisa.be.application.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationFormEmailService {

    private final JavaMailSender mailSender;

    @Value("${navisa.mail.care-template}")
    private String careMailTemplate;

    @Async("mailExecutor")
    public CompletableFuture<Boolean> sendCareEmail(String recipientEmail, String agentName) {
        try {
            String safeAgentName = HtmlUtils.htmlEscape(agentName);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipientEmail);
            helper.setSubject("[Navisa] 담당 사례 비자 발급 현황 점검 안내");

            String content = String.format(careMailTemplate, safeAgentName);

            helper.setText(content, true);
            mailSender.send(message);

            log.info("행정사 대상 비자 현황 점검 메일 전송 성공");
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            log.error("메일 전송 중 오류 발생: ", e);
            return CompletableFuture.completedFuture(false);
        }
    }
}

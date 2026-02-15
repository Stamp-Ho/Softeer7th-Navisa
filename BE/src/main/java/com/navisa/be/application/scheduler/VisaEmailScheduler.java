package com.navisa.be.application.scheduler;

import com.navisa.be.application.exception.ApplicationException;
import com.navisa.be.application.model.entity.VisaApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.application.service.ApplicationCommandService;
import com.navisa.be.application.service.ApplicationEmailService;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class VisaEmailScheduler {

    private final ApplicationFormRepository applicationFormRepository;
    private final UserRepository userRepository;
    private final ApplicationEmailService applicationEmailService;
    private final ApplicationCommandService applicationCommandService;

    @Scheduled(cron = "0 0 10 * * *") // 매일 오전 10시
    @Transactional
    public void sendFollowUpEmails() {
        LocalDate targetDate = LocalDate.now().minusDays(14);
        List<VisaApplicationForm> forms = applicationFormRepository.findAllByExportedDate(targetDate);

        for (VisaApplicationForm form : forms) {
            if (form.getAgentProfile() == null) {
                log.warn("Form ID: {} 에 배정된 행정사가 없어 메일을 발송하지 않습니다.", form.getId());
                continue;
            }

            try {
                UUID userId = form.getAgentProfile().getUserId();

                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new ApplicationException(ResponseStatus.INVALID_USER));

                String recipientEmail = user.getEmail();
                String agentName = form.getAgentProfile().getName();
                UUID formId = form.getId();

                applicationEmailService.sendCareEmail(recipientEmail, agentName)
                        .thenAccept(isSuccess -> {
                            if (Boolean.TRUE.equals(isSuccess)) {
                                applicationCommandService.updateMailSentTime(formId);
                                log.info("사후 관리 메일 발송 및 기록 완료: {} ({})", agentName, recipientEmail);
                            }
                        });

                log.info("사후 관리 메일 발송 성공: {} ({})", agentName, recipientEmail);
            } catch (Exception e) {
                log.error("메일 발송 실패 - Form ID: {}, 사유: {}", form.getId(), e.getMessage());
            }
        }
    }
}

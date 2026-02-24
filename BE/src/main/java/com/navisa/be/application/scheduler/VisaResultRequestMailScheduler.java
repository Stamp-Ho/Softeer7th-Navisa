package com.navisa.be.application.scheduler;

import com.navisa.be.application.model.entity.ApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.application.service.ApplicationFormForAgentService;
import com.navisa.be.application.service.ApplicationFormEmailService;
import com.navisa.be.chat.service.ChatRoomSearchService;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.service.UserCrudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class VisaResultRequestMailScheduler {

    private final ApplicationFormRepository applicationFormRepository;
    private final ApplicationFormEmailService applicationFormEmailService;
    private final ApplicationFormForAgentService applicationFormForAgentService;
    private final UserCrudService userCrudService;
    private final ChatRoomSearchService chatRoomSearchService;

    @Scheduled(cron = "0 0 10 * * *") // 매일 오전 10시
    @SchedulerLock(name = "VisaEmailScheduler_sendFollowUpEmails", lockAtMostFor = "10m", lockAtLeastFor = "2m")
    public void sendFollowUpEmails() {
        LocalDate targetDate = LocalDate.now().minusDays(14);
        List<ApplicationForm> forms = applicationFormRepository.findAllByExportedDate(targetDate);

        for (ApplicationForm form : forms) {
            if (form.getAgentProfile() == null) {
                log.warn("Form ID: {} 에 배정된 행정사가 없어 메일을 발송하지 않습니다.", form.getId());
                continue;
            }

            try {
                var agentProfile = form.getAgentProfile();
                var foreignerProfile = form.getForeignerProfile();

                Long chatRoomId = chatRoomSearchService.getChatRoomIdByProfiles(agentProfile, foreignerProfile);

                if (chatRoomId == null) {
                    log.warn("Form ID: {} 에 해당하는 채팅방을 찾을 수 없어 링크를 생성할 수 없습니다.", form.getId());
                    continue;
                }

                UUID userId = form.getAgentProfile().getUserId();
                User user = userCrudService.findById(userId);

                String recipientEmail = user.getEmail();
                String agentName = form.getAgentProfile().getName();
                UUID formId = form.getId();

                applicationFormEmailService.sendCareEmail(recipientEmail, agentName, chatRoomId)
                        .thenAccept(isSuccess -> {
                            if (Boolean.TRUE.equals(isSuccess)) {
                                applicationFormForAgentService.updateMailSentTime(formId);
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

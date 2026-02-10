package com.navisa.be.application.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.application.dto.response.RecentVisaFormsResponse;
import com.navisa.be.application.dto.response.VisaApplicationDetailResponse;
import com.navisa.be.application.exception.ApplicationException;
import com.navisa.be.application.model.entity.VisaApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.repository.ForeignerProfileRepository;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationQueryService {

    private final ApplicationFormRepository visaApplicationFormRepository;
    private final UserRepository userRepository;
    private final AgentProfileRepository agentProfileRepository;
    private final ForeignerProfileRepository foreignerProfileRepository;

    // 행정사의 최근 수정 문서 조회
    public List<RecentVisaFormsResponse> getRecentVisaForms(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException(ResponseStatus.USER_INVALID));

        if (!user.getUserType().equals(UserType.VALID_AGENT)) {
            throw new ApplicationException(ResponseStatus.AGENT_NOT_APPROVED);
        }

        AgentProfile agent = agentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ApplicationException(ResponseStatus.AGENT_NOT_FOUND));

        List<VisaApplicationForm> forms = visaApplicationFormRepository
                .findTop6ByAgentProfileOrderByUpdatedAtDesc(agent);

        return forms.stream()
                .map(form -> new RecentVisaFormsResponse(
                        form.getId(),
                        form.getForeignerProfile().getNickname(),
                        form.getIsDone(),
                        form.getCurrentStep(),
                        form.getProfileObjectKey(),
                        form.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy. MM. dd"))
                ))
                .toList();
    }

    // 외국인용 최신 비자신청서
    public VisaApplicationDetailResponse getLatestVisaFormForForeigner(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException(ResponseStatus.USER_INVALID));

        ForeignerProfile foreigner = foreignerProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ApplicationException(ResponseStatus.INVALID_FOREIGNER));

        VisaApplicationForm form = visaApplicationFormRepository
                .findFirstByForeignerProfileOrderByCreatedAtDesc(foreigner)
                .orElseThrow(() -> new ApplicationException(ResponseStatus.VISA_APP_FORM_NOT_FOUND));

        List<Map<String, Object>> sections = mergeSections(form);

        return new VisaApplicationDetailResponse(
                form.getId(),
                form.getProfileObjectKey(),
                form.getIsDone(),
                form.getUpdatedAt().toString(),
                form.getTotalCount(),
                form.getCurrentStep(),
                sections
        );
    }

    // 행정사용 특정 비자 신청서 조회
    public VisaApplicationDetailResponse getVisaFormForAgent(String email, UUID applicationFormId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException(ResponseStatus.USER_INVALID));

        AgentProfile agent = agentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ApplicationException(ResponseStatus.AGENT_NOT_FOUND));

        VisaApplicationForm form = visaApplicationFormRepository.findWithAgentProfileById(applicationFormId)
                .orElseThrow(() -> new ApplicationException(ResponseStatus.VISA_APP_FORM_NOT_FOUND));

        // 이 신청서의 담당 행정사가 현재 로그인한 행정사인지 확인
        if (form.getAgentProfile() == null || !form.getAgentProfile().getId().equals(agent.getId())) {
            throw new ApplicationException(ResponseStatus.FORBIDDEN);
        }

        return new VisaApplicationDetailResponse(
                form.getId(),
                form.getProfileObjectKey(),
                form.getIsDone(),
                form.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy. MM. dd")),
                form.getTotalCount(),
                form.getCurrentStep(),
                mergeSections(form)
        );
    }

    private List<Map<String, Object>> mergeSections(VisaApplicationForm form) {
        return Stream.of(
                        form.getPersonalDetail(),
                        form.getPassportInformation(),
                        form.getContactInformation(),
                        form.getMaritalStatusAndFamilyDetails(),
                        form.getEducation(),
                        form.getEmployment(),
                        form.getVisitInformation(),
                        form.getHelpInformation(),
                        form.getInviteInformation()
                )
                .map(section -> section != null ? section : Collections.<String, Object>emptyMap())
                .toList();
    }
}

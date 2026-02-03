package com.navisa.be.application.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.application.dto.response.RecentVisaFormsResponse;
import com.navisa.be.application.exception.ApplicationException;
import com.navisa.be.application.model.entity.VisaApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationQueryService {

    private final ApplicationFormRepository visaApplicationFormRepository;
    private final UserRepository userRepository;
    private final AgentProfileRepository agentProfileRepository;

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
                        form.getForeignerProfile().getProfileObjectKey(),
                        form.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy. MM. dd"))
                ))
                .toList();
    }
}

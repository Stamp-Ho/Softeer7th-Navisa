package com.navisa.be.support;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.application.model.entity.ApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.global.common.model.entity.JobCode;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class VisaApplicationFormTestFixture {

    private final ApplicationFormRepository applicationFormRepository;

    public ApplicationForm createVisaApplicationForm(AgentProfile agentProfile, ForeignerProfile foreignerProfile,
                                                     JobCode jobCode, boolean isDone) {
        ApplicationForm form = new ApplicationForm(
                agentProfile,
                foreignerProfile,
                jobCode,
                isDone,
                150,
                10);

        Map<String, Object> mockData = Map.of("name", "John Doe", "birth", "1990-01-01");
        ReflectionTestUtils.setField(form, "personalDetail", mockData);
        ReflectionTestUtils.setField(form, "passportInformation", Map.of("passportNo", "M1234567"));

        if (isDone) {
            ReflectionTestUtils.setField(form, "exportedAt", LocalDateTime.now());
        }

        return applicationFormRepository.save(form);
    }
}

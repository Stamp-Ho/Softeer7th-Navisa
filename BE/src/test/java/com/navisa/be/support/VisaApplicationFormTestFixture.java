package com.navisa.be.support;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.application.model.entity.VisaApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.common.model.entity.JobCode;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.test.util.ReflectionTestUtils;

@RequiredArgsConstructor
@Component
public class VisaApplicationFormTestFixture {

    private final ApplicationFormRepository applicationFormRepository;

    public VisaApplicationForm createVisaApplicationForm(AgentProfile agentProfile, ForeignerProfile foreignerProfile, JobCode jobCode, boolean isOnceExported) {
        return createVisaApplicationForm(agentProfile, foreignerProfile, jobCode, isOnceExported, null);
    }

    public VisaApplicationForm createVisaApplicationForm(AgentProfile agentProfile, ForeignerProfile foreignerProfile,
                                                         JobCode jobCode, boolean isOnceExported, String profileObjectKey) {
        VisaApplicationForm form = new VisaApplicationForm(
                agentProfile,
                foreignerProfile,
                jobCode,
                false,
                150,
                110);
        ReflectionTestUtils.setField(form, "isOnceExported", isOnceExported);
        if (profileObjectKey != null) {
            form.updateProfileImage(profileObjectKey);
        }
        return applicationFormRepository.save(form);
    }

    public VisaApplicationForm createVisaApplicationForm(AgentProfile agentProfile, ForeignerProfile foreignerProfile,
                                                         JobCode jobCode, boolean isDone, int currentStep, String profileObjectKey) {
        VisaApplicationForm form = new VisaApplicationForm(
                agentProfile,
                foreignerProfile,
                jobCode,
                isDone,
                150,
                currentStep);
        if (profileObjectKey != null) {
            form.updateProfileImage(profileObjectKey);
        }
        return applicationFormRepository.save(form);
    }
}

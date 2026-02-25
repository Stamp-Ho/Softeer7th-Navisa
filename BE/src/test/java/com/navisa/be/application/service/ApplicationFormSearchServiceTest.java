package com.navisa.be.application.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.application.dto.response.ApplicationFormCardResponse;
import com.navisa.be.application.dto.response.ApplicationFormDetailResponse;
import com.navisa.be.application.dto.response.RecentApplicationFormsResponse;
import com.navisa.be.application.model.entity.ApplicationForm;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.global.web.request.SliceRequest;
import com.navisa.be.global.web.response.SliceResponse;
import com.navisa.be.support.*;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;


class ApplicationFormSearchServiceTest extends IntegrationTestSupport {

    @Autowired
    private ApplicationFormSearchService applicationFormSearchService;

    @Autowired
    private AgentProfileTestFixture agentProfileTestFixture;

    @Autowired
    private UserTestFixture userTestFixture;

    @Autowired
    private VisaApplicationFormTestFixture visaApplicationFormTestFixture;

    @Autowired
    private ForeignerProfileTestFixture foreignerProfileTestFixture;

    @Test
    @DisplayName("행정사가 최근 비자신청서 조회에 성공한다")
    void getRecentApplicationForm_success() {
        // given
        User agentUser = userTestFixture.createUser("agent@test.com", UserType.VALID_AGENT);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("행정사", "주소", agentUser.getId());

        User foreignerUser = userTestFixture.createUser("foreigner@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        visaApplicationFormTestFixture.createVisaApplicationForm(agentProfile, foreignerProfile, null, true);
        visaApplicationFormTestFixture.createVisaApplicationForm(agentProfile, foreignerProfile, null, true);

        // when
        List<RecentApplicationFormsResponse> recentApplicationForms = applicationFormSearchService.getRecentApplicationForms(agentUser.getEmail());

        // then
        Assertions.assertThat(recentApplicationForms.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("행정사는 작성 중인 비자신청서 조회에 성공한다")
    void getApplicationFormForAgent_success() {
        // given
        User agentUser = userTestFixture.createUser("agent@test.com", UserType.VALID_AGENT);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("행정사", "주소", agentUser.getId());

        User foreignerUser = userTestFixture.createUser("foreigner@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        ApplicationForm firstForm = visaApplicationFormTestFixture.createVisaApplicationForm(agentProfile, foreignerProfile, null, true);
        ApplicationForm secondForm = visaApplicationFormTestFixture.createVisaApplicationForm(agentProfile, foreignerProfile, null, true);

        // when
        ApplicationFormDetailResponse response = applicationFormSearchService.getApplicationFormForAgent(agentUser.getEmail(), firstForm.getId());

        // then
        Assertions.assertThat(response.applicationFormId()).isEqualTo(firstForm.getId());
    }

    @Test
    @DisplayName("행정사는 필터링(완료)을 통해 비자신청서 목록 조회에 성공한다")
    void findApplicationFormsByFilter_complete_success() {
        // given
        User agentUser = userTestFixture.createUser("agent@test.com", UserType.VALID_AGENT);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("행정사", "주소", agentUser.getId());

        User foreignerUser = userTestFixture.createUser("foreigner@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        visaApplicationFormTestFixture.createVisaApplicationForm(agentProfile, foreignerProfile, null, false);
        ApplicationForm completedForm1 = visaApplicationFormTestFixture.createVisaApplicationForm(agentProfile, foreignerProfile, null, true);
        visaApplicationFormTestFixture.createVisaApplicationForm(agentProfile, foreignerProfile, null, false);
        ApplicationForm completedForm2 = visaApplicationFormTestFixture.createVisaApplicationForm(agentProfile, foreignerProfile, null, true);

        SliceRequest<UUID> sliceRequest = new SliceRequest<>(null, 10);
        Boolean complete = true;

        // when
        SliceResponse<ApplicationFormCardResponse, UUID> response = applicationFormSearchService.findApplicationFormsByFilter(agentUser.getEmail(), sliceRequest, complete);

        // then
        Assertions.assertThat(response.content()).hasSize(2);
        Assertions.assertThat(response.content().stream().map(ApplicationFormCardResponse::applicationFormId))
                .containsExactly(completedForm2.getId(), completedForm1.getId());
        Assertions.assertThat(response.existsNext()).isFalse();
        Assertions.assertThat(response.lastElementId()).isEqualTo(completedForm1.getId());
    }
}

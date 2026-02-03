package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.request.AgentCardRequest;
import com.navisa.be.agent.dto.response.AgentCardResponse;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.common.dto.request.SliceRequest;
import com.navisa.be.common.dto.response.SliceResponse;
import com.navisa.be.common.model.entity.JobCode;
import com.navisa.be.common.model.entity.Language;
import com.navisa.be.info.model.entity.JobGroup;
import com.navisa.be.support.AgentProfileTestFixture;
import com.navisa.be.support.IntegrationTestSupport;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.LoginType;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class AgentProfileQueryServiceTest extends IntegrationTestSupport {

    @Autowired
    private AgentProfileServiceFacade agentProfileServiceFacade;

    @Autowired
    private AgentProfileTestFixture agentProfileTestFixture;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("FILLED_FOREIGNER 유저는 전문분야 정보를 볼 수 있다")
    void findAgentProfileCardsBasedOnFilter_filledUser_shouldSeeSpeciality() {
        // given
        JobGroup jobGroup = agentProfileTestFixture.createJobGroup("기계/금속/재료");
        String uniqueJobCode = "JC-" + UUID.randomUUID().toString().substring(0, 8);
        JobCode jobCode = agentProfileTestFixture.createJobCode(uniqueJobCode, "Job Name " + uniqueJobCode, jobGroup);

        String uniqueLang = "Lang-" + UUID.randomUUID().toString().substring(0, 8);
        Language language = agentProfileTestFixture.createLanguage(uniqueLang);

        AgentProfile profile1 = agentProfileTestFixture.createAgentProfile("Agent A", "서울시 강남구", jobCode, language);
        
        // 전문분야 뱃지/요약을 위한 Summary 데이터 생성
        agentProfileTestFixture.createAgentSpecializedJobSummary(profile1.getId(), jobCode);

        // User Setup
        String email = "filled@test.com";
        userRepository.save(new User(email, "password", UserType.FILLED_FOREIGNER, LoginType.EMAIL, true));

        AgentCardRequest request = new AgentCardRequest(
                List.of("기계/금속/재료"),
                List.of("서울"),
                List.of(language.getId()));
        SliceRequest<UUID> sliceRequest = new SliceRequest<>(null, 10);

        // when
        SliceResponse<AgentCardResponse, UUID> response = agentProfileServiceFacade
                .findAgentProfileCardsBasedOnFilter(request, sliceRequest, email);

        // then
        List<AgentCardResponse> content = response.content();
        assertThat(content).hasSize(1);
        AgentCardResponse card = content.get(0);
        assertThat(card.agentId()).isEqualTo(profile1.getId());

        // 전문분야 정보가 포함되어야 함
        assertThat(card.agentSpecialityTop2()).isNotNull();
        assertThat(card.specialityJobCount()).isNotNull();
    }

    @Test
    @DisplayName("UNFILLED_FOREIGNER 유저는 전문분야 정보를 볼 수 없다 (Masking)")
    void findAgentProfileCardsBasedOnFilter_unfilledUser_shouldNotSeeSpeciality() {
        // given
        JobGroup jobGroup = agentProfileTestFixture.createJobGroup("기계/금속/재료");
        String uniqueJobCode = "JC-" + UUID.randomUUID().toString().substring(0, 8);
        JobCode jobCode = agentProfileTestFixture.createJobCode(uniqueJobCode, "Job Name " + uniqueJobCode, jobGroup);

        String uniqueLang = "Lang-" + UUID.randomUUID().toString().substring(0, 8);
        Language language = agentProfileTestFixture.createLanguage(uniqueLang);

        AgentProfile profile1 = agentProfileTestFixture.createAgentProfile("Agent A", "서울시 강남구", jobCode, language);
        // 전문분야 Summary
        agentProfileTestFixture.createAgentSpecializedJobSummary(profile1.getId(), jobCode);

        // User Setup
        String email = "unfilled@test.com";
        userRepository.save(new User(email, "password", UserType.UNFILLED_FOREIGNER, LoginType.EMAIL, true));

        AgentCardRequest request = new AgentCardRequest(
                List.of("기계/금속/재료"),
                List.of("서울"),
                List.of(language.getId()));
        SliceRequest<UUID> sliceRequest = new SliceRequest<>(null, 10);

        // when
        SliceResponse<AgentCardResponse, UUID> response = agentProfileServiceFacade
                .findAgentProfileCardsBasedOnFilter(request, sliceRequest, email);

        // then
        AgentCardResponse card = response.content().get(0);

        // 전문분야 정보가 Masking(null) 처리되어야 함
        assertThat(card.agentSpecialityTop2()).isNull();
        assertThat(card.specialityJobCount()).isNull();
    }

    @Test
    @DisplayName("마지막 페이지 조회 시 existNext가 false여야 한다")
    void findAgentProfileCardsBasedOnFilter_lastPage_integration() {
        // given
        JobGroup jobGroup = agentProfileTestFixture.createJobGroup("기계/금속/재료");
        String uniqueJobCode = "JC-" + UUID.randomUUID().toString().substring(0, 8);
        JobCode jobCode = agentProfileTestFixture.createJobCode(uniqueJobCode, "Job Name " + uniqueJobCode, jobGroup);

        String uniqueLang = "Lang-" + UUID.randomUUID().toString().substring(0, 8);
        Language language = agentProfileTestFixture.createLanguage(uniqueLang);

        AgentProfile profile1 = agentProfileTestFixture.createAgentProfile("Agent A", "서울시 강남구", jobCode, language);

        String email = "test@test.com";
        userRepository.save(new User(email, "password", UserType.FILLED_FOREIGNER, LoginType.EMAIL, true));

        AgentCardRequest request = new AgentCardRequest(
                List.of("기계/금속/재료"),
                List.of("서울"),
                List.of(language.getId()));

        // 요청 개수(10) > 데이터 개수(1)
        SliceRequest<UUID> sliceRequest = new SliceRequest<>(null, 10);

        // when
        SliceResponse<AgentCardResponse, UUID> response = agentProfileServiceFacade
                .findAgentProfileCardsBasedOnFilter(request, sliceRequest, email);

        // then
        assertThat(response.content()).hasSize(1);
        assertThat(response.existsNext()).isFalse();
    }

}

                
                
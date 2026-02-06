package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.request.AgentCardRequest;
import com.navisa.be.agent.dto.response.AgentCardResponse;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.common.dto.request.SliceRequest;
import com.navisa.be.common.dto.response.SliceResponse;
import com.navisa.be.common.model.entity.JobCode;
import com.navisa.be.common.model.entity.Language;
import com.navisa.be.agent.service.AgentProfileServiceFacade;
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
class AgentProfileServiceFacadeTest extends IntegrationTestSupport {

    @Autowired
    private AgentProfileServiceFacade agentProfileServiceFacade;

    @Autowired
    private AgentProfileTestFixture fixture;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private jakarta.persistence.EntityManager em;

    @Test
    @DisplayName("직업군(JobGroup) 필터링: 해당 직업군에 속한 직무코드를 가진 행정사를 조회한다")
    void findAgentProfileCardsBasedOnFilter_ShouldFilterByJobGroup() {
        // given
        // 1. Job Group 및 Code 생성
        JobGroup engineeringGroup = fixture.createJobGroup("엔지니어링");
        JobGroup serviceGroup = fixture.createJobGroup("서비스");

        JobCode mechanicalJob = fixture.createJobCode("J-001", "기계공학", engineeringGroup);
        JobCode hospitalityJob = fixture.createJobCode("J-002", "호텔관리", serviceGroup);

        Language lang = fixture.createLanguage("영어");

        // 2. 행정사 생성
        AgentProfile engineerAgent = fixture.createAgentProfile("Engineer Agent", "서울", mechanicalJob, lang);
        AgentProfile serviceAgent = fixture.createAgentProfile("Service Agent", "서울", hospitalityJob, lang);

        // Summary 생성
        fixture.createAgentSpecializedJobSummary(engineerAgent.getId(), mechanicalJob);
        fixture.createAgentSpecializedJobSummary(serviceAgent.getId(), hospitalityJob);

        // 3. 사용자 생성
        String email = "test@user.com";
        userRepository.save(new User(email, "pw", UserType.FILLED_FOREIGNER, LoginType.EMAIL, true));

        em.flush();
        em.clear();

        // when (엔지니어링 그룹으로 검색)
        AgentCardRequest request = new AgentCardRequest(
                List.of("엔지니어링"),
                null,
                null);
        SliceRequest<UUID> sliceRequest = new SliceRequest<>(null, 10);

        SliceResponse<AgentCardResponse, UUID> response = agentProfileServiceFacade
                .findAgentProfileCardsBasedOnFilter(request, sliceRequest, email);

        // then
        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).agentId()).isEqualTo(engineerAgent.getId());
    }

    @Test
    @DisplayName("복합 필터링: 직업군 + 지역 + 언어 조건이 모두 일치하는 행정사를 조회한다")
    void findAgentProfileCardsBasedOnFilter_ShouldFilterByAllConditions() {
        // given
        JobGroup group = fixture.createJobGroup("IT");
        JobCode devJob = fixture.createJobCode("IT-01", "개발", group);

        Language eng = fixture.createLanguage("영어");
        Language jp = fixture.createLanguage("일본어");

        // 조건 일치 Agent
        AgentProfile agent1 = fixture.createAgentProfile("Target Agent", "서울시 강남구", devJob, eng);
        fixture.createAgentSpecializedJobSummary(agent1.getId(), devJob);

        // 지역 불일치
        AgentProfile agent2 = fixture.createAgentProfile("Busan Agent", "부산 해운대구", devJob, eng);
        fixture.createAgentSpecializedJobSummary(agent2.getId(), devJob);

        // 언어 불일치
        AgentProfile agent3 = fixture.createAgentProfile("JP Agent", "서울시 서초구", devJob, jp);
        fixture.createAgentSpecializedJobSummary(agent3.getId(), devJob);

        String email = "multi@user.com";
        userRepository.save(new User(email, "pw", UserType.FILLED_FOREIGNER, LoginType.EMAIL, true));

        em.flush();
        em.clear();

        // when
        AgentCardRequest request = new AgentCardRequest(
                List.of("IT"),
                List.of("서울"),
                List.of(eng.getId()));
        SliceResponse<AgentCardResponse, UUID> response = agentProfileServiceFacade
                .findAgentProfileCardsBasedOnFilter(request, new SliceRequest<>(null, 10), email);

        // then
        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).agentName()).isEqualTo("Target Agent");
    }

    @Test
    @DisplayName("유저 타입에 따른 마스킹: UNFILLED 유저는 전문분야 상세 정보를 볼 수 없다")
    void findAgentProfileCardsBasedOnFilter_ShouldMaskDataForUnfilledUser() {
        // given
        JobGroup group = fixture.createJobGroup("Medical");
        JobCode job = fixture.createJobCode("MED-01", "Doctor", group);
        Language lang = fixture.createLanguage("English");

        AgentProfile agent = fixture.createAgentProfile("Dr. House", "Princeton", job, lang);
        fixture.createAgentSpecializedJobSummary(agent.getId(), job); // Summary 생성

        // UNFILLED 유저 생성
        String email = "unfilled@user.com";
        userRepository.save(new User(email, "pw", UserType.UNFILLED_FOREIGNER, LoginType.EMAIL, true));

        em.flush();
        em.clear();

        // when
        AgentCardRequest request = new AgentCardRequest(null, null, null);
        SliceResponse<AgentCardResponse, UUID> response = agentProfileServiceFacade
                .findAgentProfileCardsBasedOnFilter(request, new SliceRequest<>(null, 10), email);

        // then
        AgentCardResponse card = response.content().get(0);
        assertThat(card.agentSpecialityTop2()).isNull(); // 마스킹 됨
        assertThat(card.specialityJobCount()).isNull(); // 마스킹 됨
    }

    @Test
    @DisplayName("유저 타입에 따른 마스킹: FILLED 유저는 전문분야 상세 정보를 볼 수 있다")
    void findAgentProfileCardsBasedOnFilter_ShouldShowDataForFilledUser() {
        // given
        JobGroup group = fixture.createJobGroup("Legal");
        JobCode job = fixture.createJobCode("LEG-01", "Lawyer", group);
        Language lang = fixture.createLanguage("English");

        AgentProfile agent = fixture.createAgentProfile("Saul Goodman", "Albuquerque", job, lang);
        fixture.createAgentSpecializedJobSummary(agent.getId(), job);

        // FILLED 유저 생성
        String email = "filled@user.com";
        userRepository.save(new User(email, "pw", UserType.FILLED_FOREIGNER, LoginType.EMAIL, true));

        em.flush();
        em.clear();

        // when
        AgentCardRequest request = new AgentCardRequest(null, null, null);
        SliceResponse<AgentCardResponse, UUID> response = agentProfileServiceFacade
                .findAgentProfileCardsBasedOnFilter(request, new SliceRequest<>(null, 10), email);

        // then
        AgentCardResponse card = response.content().get(0);
        assertThat(card.agentSpecialityTop2()).isNotNull();
        assertThat(card.agentSpecialityTop2()).isNotEmpty();
    }
}

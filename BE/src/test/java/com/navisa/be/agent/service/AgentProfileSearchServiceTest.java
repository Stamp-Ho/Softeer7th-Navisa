package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.request.AgentCardRequest;
import com.navisa.be.agent.dto.response.AgentCardResponse;
import com.navisa.be.agent.dto.response.AgentDetailResponse;
import com.navisa.be.agent.exception.AgentException;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.model.entity.Badge;
import com.navisa.be.agent.model.enums.BadgeName;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.global.common.model.enums.ImageSize;
import com.navisa.be.global.web.request.SliceRequest;
import com.navisa.be.global.web.response.SliceResponse;
import com.navisa.be.global.common.model.entity.JobCode;
import com.navisa.be.global.common.model.entity.Language;
import com.navisa.be.global.common.model.entity.JobGroup;
import com.navisa.be.support.*;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.LoginType;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class AgentProfileSearchServiceTest extends IntegrationTestSupport {

    @Autowired
    private AgentProfileSearchService agentProfileSearchService;

    @Autowired
    private AgentProfileTestFixture agentProfileTestFixture;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTestFixture userTestFixture;

    @Autowired
    private ChatRoomTestFixture chatRoomTestFixture;

    @Autowired
    private ForeignerProfileTestFixture foreignerProfileTestFixture;

    @Autowired
    private EntityManager em;

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
        SliceResponse<AgentCardResponse, UUID> response = agentProfileSearchService
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
        SliceResponse<AgentCardResponse, UUID> response = agentProfileSearchService
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
        SliceResponse<AgentCardResponse, UUID> response = agentProfileSearchService
                .findAgentProfileCardsBasedOnFilter(request, sliceRequest, email);

        // then
        assertThat(response.content()).hasSize(1);
        assertThat(response.existsNext()).isFalse();
    }

    @Test
    @DisplayName("직업군(JobGroup) 필터링: 해당 직업군에 속한 직무코드를 가진 행정사를 조회한다")
    void findAgentProfileCardsBasedOnFilter_ShouldFilterByJobGroup() {
        // given
        // 1. Job Group 및 Code 생성
        JobGroup engineeringGroup = agentProfileTestFixture.createJobGroup("엔지니어링");
        JobGroup serviceGroup = agentProfileTestFixture.createJobGroup("서비스");

        JobCode mechanicalJob = agentProfileTestFixture.createJobCode("J-001", "기계공학", engineeringGroup);
        JobCode hospitalityJob = agentProfileTestFixture.createJobCode("J-002", "호텔관리", serviceGroup);

        Language lang = agentProfileTestFixture.createLanguage("영어");

        // 2. 행정사 생성
        AgentProfile engineerAgent = agentProfileTestFixture.createAgentProfile("Engineer Agent", "서울", mechanicalJob, lang);
        AgentProfile serviceAgent = agentProfileTestFixture.createAgentProfile("Service Agent", "서울", hospitalityJob, lang);

        // Summary 생성
        agentProfileTestFixture.createAgentSpecializedJobSummary(engineerAgent.getId(), mechanicalJob);
        agentProfileTestFixture.createAgentSpecializedJobSummary(serviceAgent.getId(), hospitalityJob);

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

        SliceResponse<AgentCardResponse, UUID> response = agentProfileSearchService
                .findAgentProfileCardsBasedOnFilter(request, sliceRequest, email);

        // then
        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).agentId()).isEqualTo(engineerAgent.getId());
    }

    @Test
    @DisplayName("복합 필터링: 직업군 + 지역 + 언어 조건이 모두 일치하는 행정사를 조회한다")
    void findAgentProfileCardsBasedOnFilter_ShouldFilterByAllConditions() {
        // given
        JobGroup group = agentProfileTestFixture.createJobGroup("IT");
        JobCode devJob = agentProfileTestFixture.createJobCode("IT-01", "개발", group);

        Language eng = agentProfileTestFixture.createLanguage("영어");
        Language jp = agentProfileTestFixture.createLanguage("일본어");

        // 조건 일치 Agent
        AgentProfile agent1 = agentProfileTestFixture.createAgentProfile("Target Agent", "서울시 강남구", devJob, eng);
        agentProfileTestFixture.createAgentSpecializedJobSummary(agent1.getId(), devJob);

        // 지역 불일치
        AgentProfile agent2 = agentProfileTestFixture.createAgentProfile("Busan Agent", "부산 해운대구", devJob, eng);
        agentProfileTestFixture.createAgentSpecializedJobSummary(agent2.getId(), devJob);

        // 언어 불일치
        AgentProfile agent3 = agentProfileTestFixture.createAgentProfile("JP Agent", "서울시 서초구", devJob, jp);
        agentProfileTestFixture.createAgentSpecializedJobSummary(agent3.getId(), devJob);

        String email = "multi@user.com";
        userRepository.save(new User(email, "pw", UserType.FILLED_FOREIGNER, LoginType.EMAIL, true));

        em.flush();
        em.clear();

        // when
        AgentCardRequest request = new AgentCardRequest(
                List.of("IT"),
                List.of("서울"),
                List.of(eng.getId()));
        SliceResponse<AgentCardResponse, UUID> response = agentProfileSearchService
                .findAgentProfileCardsBasedOnFilter(request, new SliceRequest<>(null, 10), email);

        // then
        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).agentName()).isEqualTo("Target Agent");
    }

    @Test
    @DisplayName("유저 타입에 따른 마스킹: UNFILLED 유저는 전문분야 상세 정보를 볼 수 없다")
    void findAgentProfileCardsBasedOnFilter_ShouldMaskDataForUnfilledUser() {
        // given
        JobGroup group = agentProfileTestFixture.createJobGroup("Medical");
        JobCode job = agentProfileTestFixture.createJobCode("MED-01", "Doctor", group);
        Language lang = agentProfileTestFixture.createLanguage("English");

        AgentProfile agent = agentProfileTestFixture.createAgentProfile("Dr. House", "Princeton", job, lang);
        agentProfileTestFixture.createAgentSpecializedJobSummary(agent.getId(), job); // Summary 생성

        // UNFILLED 유저 생성
        String email = "unfilled@user.com";
        userRepository.save(new User(email, "pw", UserType.UNFILLED_FOREIGNER, LoginType.EMAIL, true));

        em.flush();
        em.clear();

        // when
        AgentCardRequest request = new AgentCardRequest(null, null, null);
        SliceResponse<AgentCardResponse, UUID> response = agentProfileSearchService
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
        JobGroup group = agentProfileTestFixture.createJobGroup("Legal");
        JobCode job = agentProfileTestFixture.createJobCode("LEG-01", "Lawyer", group);
        Language lang = agentProfileTestFixture.createLanguage("English");

        AgentProfile agent = agentProfileTestFixture.createAgentProfile("Saul Goodman", "Albuquerque", job, lang);
        agentProfileTestFixture.createAgentSpecializedJobSummary(agent.getId(), job);

        // FILLED 유저 생성
        String email = "filled@user.com";
        userRepository.save(new User(email, "pw", UserType.FILLED_FOREIGNER, LoginType.EMAIL, true));

        em.flush();
        em.clear();

        // when
        AgentCardRequest request = new AgentCardRequest(null, null, null);
        SliceResponse<AgentCardResponse, UUID> response = agentProfileSearchService
                .findAgentProfileCardsBasedOnFilter(request, new SliceRequest<>(null, 10), email);

        // then
        AgentCardResponse card = response.content().get(0);
        assertThat(card.agentSpecialityTop2()).isNotNull();
        assertThat(card.agentSpecialityTop2()).isNotEmpty();
    }


    @Test
    @DisplayName("외국인은 행정사 상세 조회에 성공한다")
    void getAgentDetail_succeed_whenForeigner() {
        // given
        JobCode jobCode = agentProfileTestFixture.createJobCode("직무코드", "직무명");
        Language lang = agentProfileTestFixture.createLanguage("언어");

        User agentUser = userTestFixture.createUser("agent1@test.com", UserType.VALID_AGENT);

        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("행정사", "주소", agentUser.getId());

        agentProfileTestFixture.createAgentSpecializedJob(agentProfile, jobCode);
        agentProfileTestFixture.createAgentLanguage(agentProfile, lang);

        Badge badge = agentProfileTestFixture.createBadge(BadgeName.FAST_INFORMATION);
        agentProfileTestFixture.createAgentReview(agentProfile, 1L, badge);

        User loginUser = userTestFixture.createUser("agent2@test.com", UserType.FILLED_FOREIGNER);
        foreignerProfileTestFixture.createForeignerProfile(loginUser);

        em.flush();
        em.clear();

        // when
        AgentDetailResponse response = agentProfileSearchService.getAgentDetail(loginUser.getEmail(), agentProfile.getId());

        // then
        assertThat(response).isNotNull();

        // header 검증
        assertThat(response.header()).isNotNull();
        assertThat(response.header().top2badgeIds()).hasSize(1);
        assertThat(response.header().top2badgeIds().get(0)).isEqualTo(badge.getId());
        assertThat(response.header().comment()).isEqualTo("Comment");

        // expertise 검증
        assertThat(response.expertise()).isNotNull();
        assertThat(response.expertise().jobCodeIds()).contains(jobCode.getId());
        assertThat(response.expertise().languageIds()).contains(lang.getId());

        // agentInfo 검증
        assertThat(response.agentInfo()).isNotNull();
        assertThat(response.agentInfo().agentId()).isEqualTo(agentProfile.getId());
        assertThat(response.agentInfo().name()).isEqualTo(agentProfile.getName());
        assertThat(response.agentInfo().hasChatRoom()).isFalse();

        // reviewSummary 검증
        assertThat(response.reviewSummary()).isNotNull();
        assertThat(response.reviewSummary().totalCount()).isEqualTo(1L);
        assertThat(response.reviewSummary().strengths()).hasSize(1);
        assertThat(response.reviewSummary().strengths().get(0).badgeId()).isEqualTo(badge.getId());

        // officeInfo 검증
        assertThat(response.officeInfo()).isNotNull();
        assertThat(response.officeInfo().officeName()).isEqualTo(agentProfile.getOfficeName());
        assertThat(response.officeInfo().phoneNumber()).isEqualTo(agentProfile.getPhoneNumber());
    }

    @Test
    @DisplayName("행정사는 행정사 상세 조회에 성공한다")
    void getAgentDetail_succeed_whenAgent() {
        // given
        JobCode jobCode = agentProfileTestFixture.createJobCode("직무코드", "직무명");
        Language lang = agentProfileTestFixture.createLanguage("언어");

        User agentUser = userTestFixture.createUser("agent1@test.com", UserType.VALID_AGENT);

        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("행정사", "주소", agentUser.getId());

        agentProfileTestFixture.createAgentSpecializedJob(agentProfile, jobCode);
        agentProfileTestFixture.createAgentLanguage(agentProfile, lang);

        Badge badge = agentProfileTestFixture.createBadge(BadgeName.FAST_INFORMATION);
        agentProfileTestFixture.createAgentReview(agentProfile, 1L, badge);

        User loginUser = userTestFixture.createUser("agent2@test.com", UserType.VALID_AGENT);
        agentProfileTestFixture.createAgentProfile("행정사2", "주소2", loginUser.getId());

        em.flush();
        em.clear();

        // when
        AgentDetailResponse response = agentProfileSearchService.getAgentDetail(loginUser.getEmail(), agentProfile.getId());

        // then
        assertThat(response).isNotNull();

        // header 검증
        assertThat(response.header()).isNotNull();
        assertThat(response.header().top2badgeIds()).hasSize(1);
        assertThat(response.header().top2badgeIds().get(0)).isEqualTo(badge.getId());
        assertThat(response.header().comment()).isEqualTo("Comment");

        // expertise 검증
        assertThat(response.expertise()).isNotNull();
        assertThat(response.expertise().jobCodeIds()).contains(jobCode.getId());
        assertThat(response.expertise().languageIds()).contains(lang.getId());

        // agentInfo 검증
        assertThat(response.agentInfo()).isNotNull();
        assertThat(response.agentInfo().agentId()).isEqualTo(agentProfile.getId());
        assertThat(response.agentInfo().name()).isEqualTo(agentProfile.getName());
        assertThat(response.agentInfo().hasChatRoom()).isFalse();
        assertThat(response.agentInfo().profileImageUrl()).contains(ImageSize.MEDIUM.getPath());

        // reviewSummary 검증
        assertThat(response.reviewSummary()).isNotNull();
        assertThat(response.reviewSummary().totalCount()).isEqualTo(1L);
        assertThat(response.reviewSummary().strengths()).hasSize(1);
        assertThat(response.reviewSummary().strengths().get(0).badgeId()).isEqualTo(badge.getId());

        // officeInfo 검증
        assertThat(response.officeInfo()).isNotNull();
        assertThat(response.officeInfo().officeName()).isEqualTo(agentProfile.getOfficeName());
        assertThat(response.officeInfo().phoneNumber()).isEqualTo(agentProfile.getPhoneNumber());
    }

    @Test
    @DisplayName("행정사 상세 조회는 행정사가 없으면 예외가 발생한다")
    void getAgentDetail_shouldThrowException_whenNoAgent() {
        // given
        UUID UNKNOWN_AGENT_ID = UUID.randomUUID();

        User loginUser = userTestFixture.createUser("agent@test.com", UserType.FILLED_FOREIGNER);

        // when & then
        assertThatThrownBy(() -> agentProfileSearchService.getAgentDetail(loginUser.getEmail(), UNKNOWN_AGENT_ID))
                .isInstanceOf(AgentException.class);
    }

    @Test
    @DisplayName("행정사 상세 조회는 채팅방이 있으면 채팅 관련 정보를 반환한다")
    void getAgentDetail_shouldReturnChatRoomInfo() {
        // given
        JobCode jobCode = agentProfileTestFixture.createJobCode("직무코드", "직무명");
        Language lang = agentProfileTestFixture.createLanguage("언어");

        User agentUser = userTestFixture.createUser("agent1@test.com", UserType.VALID_AGENT);

        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("행정사", "주소", agentUser.getId());

        agentProfileTestFixture.createAgentSpecializedJob(agentProfile, jobCode);
        agentProfileTestFixture.createAgentLanguage(agentProfile, lang);

        Badge badge = agentProfileTestFixture.createBadge(BadgeName.FAST_INFORMATION);
        agentProfileTestFixture.createAgentReview(agentProfile, 1L, badge);

        User loginUser = userTestFixture.createUser("agent2@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(loginUser);

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT, ZonedDateTime.now());

        // when
        AgentDetailResponse response = agentProfileSearchService.getAgentDetail(loginUser.getEmail(), agentProfile.getId());

        // then
        assertThat(response).isNotNull();

        // agentInfo 검증
        assertThat(response.agentInfo()).isNotNull();
        assertThat(response.agentInfo().agentId()).isEqualTo(agentProfile.getId());
        assertThat(response.agentInfo().name()).isEqualTo(agentProfile.getName());
        assertThat(response.agentInfo().hasChatRoom()).isTrue();
        assertThat(response.agentInfo().hasBlocked()).isFalse();
        assertThat(response.agentInfo().chatRoomId()).isEqualTo(chatRoom.getId());
    }
}

package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.response.GetAgentDetailResponse;
import com.navisa.be.agent.exception.AgentException;
import com.navisa.be.agent.model.entity.*;
import com.navisa.be.agent.model.enums.BadgeName;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import com.navisa.be.common.model.entity.JobCode;
import com.navisa.be.common.model.entity.Language;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.storage.model.enums.ImageSize;
import com.navisa.be.support.*;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class AgentProfileQueryServiceIntegrationTest extends IntegrationTestSupport {
    @Autowired
    private AgentProfileQueryService agentProfileQueryService;

    @Autowired
    private AgentProfileTestFixture agentProfileTestFixture;

    @Autowired
    private UserTestFixture userTestFixture;

    @Autowired
    private ChatRoomTestFixture chatRoomTestFixture;

    @Autowired
    private ForeignerProfileTestFixture foreignerProfileTestFixture;

    @Autowired
    private EntityManager em;

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
        agentProfileTestFixture.createAgentReview(agentProfile, badge);

        User loginUser = userTestFixture.createUser("agent2@test.com", UserType.FILLED_FOREIGNER);
        foreignerProfileTestFixture.createForeignerProfile(loginUser);

        em.flush();
        em.clear();

        // when
        GetAgentDetailResponse response = agentProfileQueryService.getAgentDetail(loginUser.getEmail(), agentProfile.getId());

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
        assertThat(response.agentInfo().name()).isEqualTo("행정사");
        assertThat(response.agentInfo().hasChatRoom()).isFalse();

        // reviewSummary 검증
        assertThat(response.reviewSummary()).isNotNull();
        assertThat(response.reviewSummary().totalCount()).isEqualTo(1L);
        assertThat(response.reviewSummary().strengths()).hasSize(1);
        assertThat(response.reviewSummary().strengths().get(0).badgeId()).isEqualTo(badge.getId());

        // officeInfo 검증
        assertThat(response.officeInfo()).isNotNull();
        assertThat(response.officeInfo().officeName()).isEqualTo("Office Name");
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
        agentProfileTestFixture.createAgentReview(agentProfile, badge);

        User loginUser = userTestFixture.createUser("agent2@test.com", UserType.VALID_AGENT);
        agentProfileTestFixture.createAgentProfile("행정사2", "주소2", loginUser.getId());

        em.flush();
        em.clear();

        // when
        GetAgentDetailResponse response = agentProfileQueryService.getAgentDetail(loginUser.getEmail(), agentProfile.getId());

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
        assertThat(response.agentInfo().name()).isEqualTo("행정사");
        assertThat(response.agentInfo().hasChatRoom()).isFalse();
        assertThat(response.agentInfo().profileImageUrl()).contains(ImageSize.MEDIUM.getPath());

        // reviewSummary 검증
        assertThat(response.reviewSummary()).isNotNull();
        assertThat(response.reviewSummary().totalCount()).isEqualTo(1L);
        assertThat(response.reviewSummary().strengths()).hasSize(1);
        assertThat(response.reviewSummary().strengths().get(0).badgeId()).isEqualTo(badge.getId());

        // officeInfo 검증
        assertThat(response.officeInfo()).isNotNull();
        assertThat(response.officeInfo().officeName()).isEqualTo("Office Name");
    }

    @Test
    @DisplayName("행정사 상세 조회는 행정사가 없으면 예외가 발생한다")
    void getAgentDetail_shouldThrowException_whenNoAgent() {
        // given
        UUID UNKNOWN_AGENT_ID = UUID.randomUUID();

        User loginUser = userTestFixture.createUser("agent@test.com", UserType.FILLED_FOREIGNER);

        // when & then
        assertThatThrownBy(() -> agentProfileQueryService.getAgentDetail(loginUser.getEmail(), UNKNOWN_AGENT_ID))
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
        agentProfileTestFixture.createAgentReview(agentProfile, badge);

        User loginUser = userTestFixture.createUser("agent2@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(loginUser);

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT, ZonedDateTime.now());

        // when
        GetAgentDetailResponse response = agentProfileQueryService.getAgentDetail(loginUser.getEmail(), agentProfile.getId());

        // then
        assertThat(response).isNotNull();

        // agentInfo 검증
        assertThat(response.agentInfo()).isNotNull();
        assertThat(response.agentInfo().agentId()).isEqualTo(agentProfile.getId());
        assertThat(response.agentInfo().name()).isEqualTo("행정사");
        assertThat(response.agentInfo().hasChatRoom()).isTrue();
        assertThat(response.agentInfo().hasBlocked()).isFalse();
        assertThat(response.agentInfo().chatRoomId()).isEqualTo(chatRoom.getId());
    }
}

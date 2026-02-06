package com.navisa.be.agent.service;

import com.navisa.be.agent.event.ReviewCreatedBadgeEvent;
import com.navisa.be.agent.event.ReviewCreatedSpecializedJobEvent;
import com.navisa.be.agent.exception.AgentException;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import com.navisa.be.chat.model.enums.ProposalStatus;
import com.navisa.be.support.*;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.agent.repository.AgentBadgeRepository;
import com.navisa.be.agent.repository.AgentReviewRepository;
import com.navisa.be.agent.model.entity.AgentReview;
import com.navisa.be.agent.model.entity.AgentBadge;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.model.entity.Badge;
import com.navisa.be.common.model.entity.JobCode;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.agent.dto.request.CreateAgentReviewRequest;
import com.navisa.be.agent.model.enums.BadgeName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RecordApplicationEvents // 이벤트 발생 기록을 활성화
@Transactional
class AgentReviewServiceTest extends IntegrationTestSupport {

    @Autowired
    private AgentReviewService agentReviewService;

    @Autowired
    private UserTestFixture userTestFixture;

    @Autowired
    private AgentProfileTestFixture agentProfileTestFixture;

    @Autowired
    private ForeignerProfileTestFixture foreignerProfileTestFixture;

    @Autowired
    private ChatRoomTestFixture chatRoomTestFixture;

    @Autowired
    private ProposalTestFixture proposalTestFixture;

    @Autowired
    private VisaApplicationFormTestFixture visaApplicationFormTestFixture;

    @Autowired
    private AgentBadgeRepository agentBadgeRepository;

    @Autowired
    private AgentReviewRepository agentReviewRepository;

    @Autowired
    private ApplicationEvents events;

    @Test
    @DisplayName("외국인은 행정사 리뷰 저장에 성공한다")
    void createAgentReview_shouldSucceed() {
        // given
        User foreignerUser = userTestFixture.createUser("foreigner@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        User agentUser = userTestFixture.createUser("agent@test.com", UserType.VALID_AGENT);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent Name", "Seoul",
                agentUser.getId());

        Badge badge = agentProfileTestFixture.createBadge(BadgeName.KIND_CONSULTATION);
        JobCode jobCode = agentProfileTestFixture.createJobCode("IT", "IT Job");

        // ChatRoom 저장
        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT, ZonedDateTime.now());

        // Proposal 저장, status는 MATCHED
        proposalTestFixture.createProposal(chatRoom, foreignerProfile.getId(), ProposalStatus.MATCHED);

        // VisaApplicationForm 저장, isOnceExported는 true
        visaApplicationFormTestFixture.createVisaApplicationForm(agentProfile, foreignerProfile, jobCode, true);

        CreateAgentReviewRequest request = new CreateAgentReviewRequest(Arrays.asList(badge.getId()), agentProfile.getId());

        // when
        agentReviewService.createAgentReview(foreignerUser.getEmail(), request);

        // then

        // DB에 저장된 AgentProfile, AgentBadgeSummary, AgentSpecializedJobSummary 결과 검증
        List<AgentReview> reviews = agentReviewRepository.findAll();
        assertEquals(1, reviews.size());
        assertEquals(agentProfile.getId(), reviews.get(0).getAgentProfileId());
        assertEquals(foreignerProfile.getId(), reviews.get(0).getForeignerProfileId());

        List<AgentBadge> agentBadges = agentBadgeRepository.findAll();
        assertEquals(1, agentBadges.size());
        assertEquals(badge.getBadgeName(), agentBadges.get(0).getBadge().getBadgeName());

        // 특정 타입의 이벤트가 몇 번 발행되었는지 확인
        long badgeEventCount = events.stream(ReviewCreatedBadgeEvent.class).count();
        long jobEventCount = events.stream(ReviewCreatedSpecializedJobEvent.class).count();

        assertThat(badgeEventCount).isEqualTo(1);
        assertThat(jobEventCount).isEqualTo(1);

        // 이벤트 내부 데이터까지 상세 검증
        ReviewCreatedBadgeEvent publishedEvent = events.stream(ReviewCreatedBadgeEvent.class)
                .findFirst()
                .orElseThrow();

        assertThat(publishedEvent.agentId()).isEqualTo(request.agentId());
        assertThat(publishedEvent.badgeIds()).containsAll(request.badgeIdList());
    }

    @Test
    @DisplayName("행정사는 행정사 리뷰 작성에 실패한다")
    void createAgentReview_shouldThrowException_whenAgent() {
        // given
        User agentUser = userTestFixture.createUser("agent@test.com", UserType.VALID_AGENT);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent Name", "Seoul",
                agentUser.getId());

        Badge badge = agentProfileTestFixture.createBadge(BadgeName.KIND_CONSULTATION);

        CreateAgentReviewRequest request = new CreateAgentReviewRequest(Arrays.asList(badge.getId()), agentProfile.getId());

        // when & then
        assertThatThrownBy(() -> agentReviewService.createAgentReview(agentUser.getEmail(), request))
                .isInstanceOf(AgentException.class);
    }

    @Test
    @DisplayName("리뷰가 이미 존재하면 행정사 리뷰 작성시에 예외가 발생한다")
    void createAgentReview_shouldThrowException_whenReviewAlreadyExists() {
        // given
        User foreignerUser = userTestFixture.createUser("foreigner@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        User agentUser = userTestFixture.createUser("agent@test.com", UserType.VALID_AGENT);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent Name", "Seoul",
                agentUser.getId());

        Badge badge = agentProfileTestFixture.createBadge(BadgeName.KIND_CONSULTATION);
        JobCode jobCode = agentProfileTestFixture.createJobCode("IT", "IT Job");

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT, ZonedDateTime.now());
        proposalTestFixture.createProposal(chatRoom, foreignerProfile.getId(), ProposalStatus.MATCHED);
        visaApplicationFormTestFixture.createVisaApplicationForm(agentProfile, foreignerProfile, jobCode, true);

        CreateAgentReviewRequest request = new CreateAgentReviewRequest(Arrays.asList(badge.getId()), agentProfile.getId());
        agentReviewService.createAgentReview(foreignerUser.getEmail(), request);

        // when & then
        assertThatThrownBy(() -> agentReviewService.createAgentReview(foreignerUser.getEmail(), request))
                .isInstanceOf(AgentException.class);
    }
}

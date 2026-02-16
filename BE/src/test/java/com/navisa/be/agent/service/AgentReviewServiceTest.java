package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.request.CreateAgentReviewRequest;
import com.navisa.be.agent.dto.response.FeedbackResponse;
import com.navisa.be.agent.event.ReviewCreatedBadgeEvent;
import com.navisa.be.agent.event.ReviewCreatedSpecializedJobEvent;
import com.navisa.be.agent.exception.AgentException;
import com.navisa.be.agent.model.entity.AgentBadge;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.model.entity.AgentReview;
import com.navisa.be.agent.model.entity.Badge;
import com.navisa.be.agent.model.enums.BadgeName;
import com.navisa.be.agent.repository.AgentBadgeRepository;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.agent.repository.AgentReviewRepository;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import com.navisa.be.chat.model.enums.ProposalStatus;
import com.navisa.be.foreigner.exception.ForeignerException;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.model.entity.ForeignerSimilarity;
import com.navisa.be.foreigner.repository.ForeignerSimilarityRepository;
import com.navisa.be.global.common.model.entity.JobCode;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.support.*;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.LoginType;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RecordApplicationEvents // 이벤트 발생 기록을 활성화
@Transactional
class AgentReviewServiceTest extends IntegrationTestSupport {

    @Autowired
    private AgentReviewService agentReviewService;

    @Autowired
    private AgentReviewRepository agentReviewRepository;

    @Autowired
    private AgentProfileRepository agentProfileRepository;

    @Autowired
    private UserRepository userRepository;

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
    private ApplicationEvents events;

    @Autowired
    private ForeignerSimilarityRepository foreignerSimilarityRepository;

    @Test
    @DisplayName("외국인은 행정사 리뷰 저장에 성공한다")
    void createAgentReview_shouldSucceed() {
        // given
        User foreignerUser = userTestFixture.createUser("foreigner@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        foreignerSimilarityRepository.deleteAllInBatch();

        User agentUser = userTestFixture.createUser("agent@test.com", UserType.VALID_AGENT);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent Name", "Seoul",
                agentUser.getId());

        Badge badge = agentProfileTestFixture.createBadge(BadgeName.KIND_CONSULTATION);
        JobCode jobCode = agentProfileTestFixture.createJobCode("IT", "IT Job");

        long[] jobIds = {jobCode.getId()};
        double[] sims = {1.0};
        ForeignerSimilarity similarity = new ForeignerSimilarity(null, foreignerProfile.getId(), sims, jobIds);
        foreignerSimilarityRepository.save(similarity);

        // ChatRoom 저장
        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT, ZonedDateTime.now());

        // Proposal 저장, status는 MATCHED
        proposalTestFixture.createProposal(chatRoom, foreignerProfile.getId(), ProposalStatus.MATCHED);

        // VisaApplicationForm 저장, isOnceExported는 true
        visaApplicationFormTestFixture.createVisaApplicationForm(agentProfile, foreignerProfile, jobCode, true);

        CreateAgentReviewRequest request = new CreateAgentReviewRequest(Arrays.asList(badge.getId()), agentProfile.getId());

        // when
        agentReviewService.registerAgentReview(foreignerUser.getEmail(), request);

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
        assertThatThrownBy(() -> agentReviewService.registerAgentReview(agentUser.getEmail(), request))
                .isInstanceOf(ForeignerException.class);
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

        foreignerSimilarityRepository.deleteAllInBatch();
        long[] jobIds = {jobCode.getId()};
        double[] sims = {1.0};
        ForeignerSimilarity similarity = new ForeignerSimilarity(null, foreignerProfile.getId(), sims, jobIds);
        foreignerSimilarityRepository.save(similarity);

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT, ZonedDateTime.now());
        proposalTestFixture.createProposal(chatRoom, foreignerProfile.getId(), ProposalStatus.MATCHED);
        visaApplicationFormTestFixture.createVisaApplicationForm(agentProfile, foreignerProfile, jobCode, true);

        CreateAgentReviewRequest request = new CreateAgentReviewRequest(Arrays.asList(badge.getId()), agentProfile.getId());
        agentReviewService.registerAgentReview(foreignerUser.getEmail(), request);

        // when & then
        assertThatThrownBy(() -> agentReviewService.registerAgentReview(foreignerUser.getEmail(), request))
                .isInstanceOf(AgentException.class);
    }

    @Test
    @DisplayName("리뷰 작성 시 외국인의 유사도(Similarity) 데이터를 기반으로 상위 3개 직무의 상대 비율(ria)을 계산하여 이벤트를 발행한다")
    void createAgentReview_shouldCalculateRiaAndPublishEvent() {
        User foreignerUser = userTestFixture.createUser("foreigner@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        User agentUser = userTestFixture.createUser("agent@test.com", UserType.VALID_AGENT);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent", "Seoul", agentUser.getId());

        Badge badge = agentProfileTestFixture.createBadge(BadgeName.KIND_CONSULTATION);

        JobCode j1 = agentProfileTestFixture.createJobCode("IT_01", "Developer");
        JobCode j2 = agentProfileTestFixture.createJobCode("IT_02", "Designer");
        JobCode j3 = agentProfileTestFixture.createJobCode("IT_03", "Manager");
        JobCode j4 = agentProfileTestFixture.createJobCode("IT_04", "ETC");

        // 외국인 유사도 데이터 저장
        foreignerSimilarityRepository.deleteAll();
        long[] jobIds = {j1.getId(), j2.getId(), j3.getId(), j4.getId()};
        double[] sims = {0.6, 0.3, 0.1, 0.05};
        ForeignerSimilarity similarity = new ForeignerSimilarity(null, foreignerProfile.getId(), sims, jobIds);
        foreignerSimilarityRepository.save(similarity);

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT, ZonedDateTime.now());
        proposalTestFixture.createProposal(chatRoom, foreignerProfile.getId(), ProposalStatus.MATCHED);
        visaApplicationFormTestFixture.createVisaApplicationForm(agentProfile, foreignerProfile, j1, true);

        CreateAgentReviewRequest request = new CreateAgentReviewRequest(List.of(badge.getId()), agentProfile.getId());

        // When
        agentReviewService.registerAgentReview(foreignerUser.getEmail(), request);

        // Then
        ReviewCreatedSpecializedJobEvent jobEvent = events.stream(ReviewCreatedSpecializedJobEvent.class)
                .findFirst()
                .orElseThrow(() -> new AssertionError("이벤트가 발행되지 않았습니다."));

        // 상위 3개 직무 ID 검증
        assertThat(jobEvent.specializedJobIds()).containsExactly(j1.getId(), j2.getId(), j3.getId());

        List<Double> riaList = jobEvent.relativeRatios();
        assertThat(riaList.get(0)).isEqualTo(0.6);
        assertThat(riaList.get(1)).isEqualTo(0.3);
        assertThat(riaList.get(2)).isEqualTo(0.1);
    }

    @Test
    @DisplayName("외국인은 행정사에 대한 피드백(텍스트) 등록에 성공한다")
    void createAgentFeedback_shouldSucceed() {
        // given
        User foreignerUser = userTestFixture.createUser("foreigner_feedback@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        User agentUser = userTestFixture.createUser("agent_feedback@test.com", UserType.VALID_AGENT);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent Name", "Seoul", agentUser.getId());

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT, ZonedDateTime.now());
        var proposal = proposalTestFixture.createProposal(chatRoom, foreignerUser.getId(), ProposalStatus.MATCHED);

        AgentReview review = new AgentReview(agentProfile.getId(), foreignerProfile.getId(), proposal.getId());
        agentReviewRepository.save(review);

        String feedbackContent = "상담이 매우 구체적이고 전문적이어서 큰 도움이 되었습니다.";

        // when
        agentReviewService.createAgentFeedback(foreignerUser.getEmail(), feedbackContent);

        // then
        AgentReview updatedReview = agentReviewRepository.findByProposalId(proposal.getId()).orElseThrow();
        assertThat(updatedReview.getFeedbackContent()).isEqualTo(feedbackContent);
    }

    @Test
    @DisplayName("피드백 등록 시, 해당 제안서에 대한 리뷰가 존재하지 않으면 예외가 발생한다")
    void createAgentFeedback_shouldThrowException_whenReviewNotFound() {
        // given
        User foreignerUser = userTestFixture.createUser("no_review@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        User agentUser = userTestFixture.createUser("agent_no_review@test.com", UserType.VALID_AGENT);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent Name", "Seoul", agentUser.getId());

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT, ZonedDateTime.now());
        proposalTestFixture.createProposal(chatRoom, foreignerUser.getId(), ProposalStatus.MATCHED);

        // when & then
        assertThatThrownBy(() -> agentReviewService.createAgentFeedback(foreignerUser.getEmail(), "내용"))
                .isInstanceOf(AgentException.class);
    }

    @Test
    @DisplayName("이미 피드백이 등록된 리뷰에 다시 피드백을 등록하려 하면 예외가 발생한다")
    void createAgentFeedback_shouldThrowException_whenFeedbackAlreadyExists() {
        // given
        User foreignerUser = userTestFixture.createUser("already_feedback@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        User agentUser = userTestFixture.createUser("agent_already@test.com", UserType.VALID_AGENT);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent Name", "Seoul", agentUser.getId());

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT, ZonedDateTime.now());
        var proposal = proposalTestFixture.createProposal(chatRoom, foreignerUser.getId(), ProposalStatus.MATCHED);

        AgentReview review = new AgentReview(agentProfile.getId(), foreignerProfile.getId(), proposal.getId());
        review.updateFeedback("Existing feedback content");
        agentReviewRepository.save(review);

        // when & then
        assertThatThrownBy(() -> agentReviewService.createAgentFeedback(foreignerUser.getEmail(), "New feedback content"))
                .isInstanceOf(AgentException.class);
    }

    @DisplayName("최신순으로 등록된 행정사 리뷰 3개를 조회하고 작성자 정보를 매핑한다.")
    @Test
    void getLatestFeedbacks_Success() {
        // given
        UUID mockForeignerUserId = UUID.randomUUID();

        User user = userTestFixture.createUser("test@test.com", UserType.VALID_AGENT);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("김행정", "서울", user.getId());

        for (int i = 1; i <= 5; i++) {
            AgentReview review = new AgentReview(
                    agentProfile.getId(),
                    mockForeignerUserId,
                    (long) i,
                    "피드백 내용 " + i,
                    new double[] { 0.8, 0.9 });

            agentReviewRepository.save(review);

            ReflectionTestUtils.setField(review, "createdAt", LocalDateTime.now().plusSeconds(i));
            agentReviewRepository.saveAndFlush(review);
        }

        // when
        List<FeedbackResponse> result = agentReviewService.getLatestFeedbacks();

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).feedbackContent()).isEqualTo("피드백 내용 5");
        assertThat(result.get(0).writerName()).isEqualTo("김행정");
    }

    @DisplayName("등록된 리뷰가 하나도 없을 경우 AGENT_REVIEW_NOT_FOUND 예외가 발생한다.")
    @Test
    void getLatestFeedbacks_NotFound() {
        // given (리뷰를 저장하지 않음)

        // when & then
        assertThatThrownBy(() -> agentReviewService.getLatestFeedbacks())
                .isInstanceOf(AgentException.class)
                .hasMessageContaining(ResponseStatus.AGENT_REVIEW_NOT_FOUND.getMessage());
    }
}
package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.request.CreateAgentReviewRequest;
import com.navisa.be.agent.event.ReviewCreatedBadgeEvent;
import com.navisa.be.agent.event.ReviewCreatedSpecializedJobEvent;
import com.navisa.be.agent.exception.AgentException;
import com.navisa.be.agent.model.entity.AgentBadge;
import com.navisa.be.agent.model.entity.AgentReview;
import com.navisa.be.agent.model.entity.Badge;
import com.navisa.be.agent.repository.AgentBadgeRepository;
import com.navisa.be.agent.repository.AgentReviewRepository;
import com.navisa.be.agent.repository.BadgeRepository;
import com.navisa.be.application.model.entity.VisaApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.entity.Proposal;
import com.navisa.be.chat.repository.ChatRoomRepository;
import com.navisa.be.chat.repository.ProposalRepository;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.model.entity.ForeignerSimilarity;
import com.navisa.be.foreigner.repository.ForeignerProfileRepository;
import com.navisa.be.foreigner.repository.ForeignerSimilarityRepository;
import com.navisa.be.recommendation.calculator.ReviewReliabilityCalculator;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AgentReviewService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ForeignerProfileRepository foreignerProfileRepository;
    private final ProposalRepository proposalRepository;
    private final AgentReviewRepository agentReviewRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final AgentBadgeRepository agentBadgeRepository;
    private final BadgeRepository badgeRepository;
    private final ForeignerSimilarityRepository foreignerSimilarityRepository;
    private final ApplicationFormRepository applicationFormRepository;
    private final ReviewReliabilityCalculator reliabilityCalculator;

    @Transactional
    public void createAgentReview(String loginUserEmail, CreateAgentReviewRequest request) {
        User loginUser = userRepository.findByEmail(loginUserEmail)
                .orElseThrow(() -> new AgentException(ResponseStatus.USER_INVALID));

        ForeignerProfile foreignerProfile = foreignerProfileRepository.findByUserId(loginUser.getId())
                .orElseThrow(() -> new AgentException(ResponseStatus.INVALID_FOREIGNER));

        ChatRoom chatRoom = chatRoomRepository.findByAgentIdAndForeignerId(request.agentId(), foreignerProfile.getId())
                .orElseThrow(() -> new AgentException(ResponseStatus.PROPOSAL_NOT_FOUND));

        Proposal proposal = proposalRepository.findFirstByChatRoomOrderByIdDesc(chatRoom)
                .orElseThrow(() -> new AgentException(ResponseStatus.PROPOSAL_NOT_FOUND));

        // 리뷰가 존재하는지 여부를 확인
        if (agentReviewRepository.existsByProposalId(proposal.getId())) {
            throw new AgentException(ResponseStatus.REVIEW_ALREADY_EXISTS);
        }

        // 첫번째 내보내기 여부 확인
        VisaApplicationForm form = applicationFormRepository.findCurrentAppFormNative(foreignerProfile.getId(), request.agentId())
                .orElseThrow(() -> new AgentException(ResponseStatus.VISA_APP_FORM_NOT_FOUND));

        if (!form.isDone()) {
            throw new AgentException(ResponseStatus.NOT_ALLOWED_TO_REVIEW);
        }

        // 뱃지가 모두 존재하는지 확인
        Set<Long> requestedIds = new HashSet<>(request.badgeIdList());
        List<Badge> badges = badgeRepository.findAllByIdIn(requestedIds.stream().toList());
        if (badges.size() < requestedIds.size()) {
            throw new AgentException(ResponseStatus.BADGE_NOT_FOUND);
        }

        // 리뷰를 저장
        AgentReview agentReview = new AgentReview(request.agentId(), foreignerProfile.getId(), proposal.getId());
        agentReviewRepository.save(agentReview);

        // 리뷰 뱃지를 저장
        List<AgentBadge> agentBadges = badges.stream()
                .map(badge -> new AgentBadge(badge, agentReview))
                .toList();
        agentBadgeRepository.saveAll(agentBadges);

        // 뱃지 summary 업데이트를 위한 이벤트 발행
        ReviewCreatedBadgeEvent reviewCreatedBadgeEvent = new ReviewCreatedBadgeEvent(
                request.agentId(),
                badges.stream().map(Badge::getId).toList()
        );
        eventPublisher.publishEvent(reviewCreatedBadgeEvent);

        // Step 4-1: 행정사 특화 분야 추천을 위한 상대 신뢰도(ria) 계산
        ForeignerSimilarity similarity = foreignerSimilarityRepository.findByForeignerId(foreignerProfile.getId())
                .orElseThrow(() -> new AgentException(ResponseStatus.INVALID_FOREIGNER));

        // 상위 3개 데이터 추출
        List<Long> allJobCodeIds = Arrays.stream(similarity.getJobCodeIdList()).boxed().toList();
        List<Double> allSimilarities = Arrays.stream(similarity.getSimilarityList()).boxed().toList();

        int topLimit = Math.min(3, allSimilarities.size());
        List<Long> top3JobIds = allJobCodeIds.subList(0, topLimit);
        List<Double> top3Similarities = allSimilarities.subList(0, topLimit);

        // r_i,a = w_i,a / Σ(w_i,k) 계산
        List<Double> relativeRatios = top3Similarities.stream()
                .map(targetW -> reliabilityCalculator.calculateRelativeRatio(targetW, top3Similarities))
                .collect(Collectors.toList());

        // Step 4-2 처리를 위한 이벤트 발행 (ria 리스트 포함)
        ReviewCreatedSpecializedJobEvent reviewCreatedSpecializedJobEvent = new ReviewCreatedSpecializedJobEvent(
                request.agentId(),
                top3JobIds,
                relativeRatios
        );
        eventPublisher.publishEvent(reviewCreatedSpecializedJobEvent);
    }
}

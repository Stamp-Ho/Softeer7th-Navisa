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

    @Transactional
    public void createAgentReview(String loginUserEmail, CreateAgentReviewRequest request) {
        User loginUser = userRepository.findByEmail(loginUserEmail)
                .orElseThrow(() -> new AgentException(ResponseStatus.USER_INVALID));

        ForeignerProfile foreignerProfile = foreignerProfileRepository.findByUserId(loginUser.getId())
                .orElseThrow(() -> new AgentException(ResponseStatus.INVALID_FOREIGNER));

        ChatRoom chatRoom = chatRoomRepository.findByAgentIdAndForeignerId(request.agentId(), foreignerProfile.getId())
                .orElseThrow(() -> new AgentException(ResponseStatus.PROPOSAL_NOT_FOUND));

        Proposal proposal = proposalRepository.findFirstByChatRoomOrderByCreatedAtDesc(chatRoom)
                .orElseThrow(() -> new AgentException(ResponseStatus.PROPOSAL_NOT_FOUND));

        // 리뷰가 존재하는지 여부를 확인
        if (agentReviewRepository.existsByProposalId(proposal.getId())) {
            throw new AgentException(ResponseStatus.REVIEW_ALREADY_EXISTS);
        }

        // 첫번째 내보내기 여부 확인
        VisaApplicationForm form = applicationFormRepository.findCurrentAppFormNative(foreignerProfile.getId(), request.agentId())
                .orElseThrow(() -> new AgentException(ResponseStatus.VISA_APP_FORM_NOT_FOUND));

        if (!form.getIsOnceExported()) {
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

        // 행정사의 top2 특화 직무 업데이트를 위한 이벤트 발행
        ForeignerSimilarity similarity = foreignerSimilarityRepository.findByForeignerId(foreignerProfile.getId())
                .orElseThrow(() -> new AgentException(ResponseStatus.INVALID_FOREIGNER));

        ReviewCreatedSpecializedJobEvent reviewCreatedSpecializedJobEvent = new ReviewCreatedSpecializedJobEvent(
                request.agentId(),
                Arrays.stream(similarity.getJobCodeIdList()).boxed().toList()
        );
        eventPublisher.publishEvent(reviewCreatedSpecializedJobEvent);

        // todo 행정사 추천을 위한 파라미터 계산 step4, step5 호출
    }
}


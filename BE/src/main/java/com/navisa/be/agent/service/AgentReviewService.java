package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.request.CreateAgentReviewRequest;
import com.navisa.be.agent.dto.response.FeedbackResponse;
import com.navisa.be.agent.dto.response.ReviewReliabilityResponse;
import com.navisa.be.agent.event.ReviewCreatedBadgeEvent;
import com.navisa.be.agent.event.ReviewCreatedSpecializedJobEvent;
import com.navisa.be.agent.exception.AgentException;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.model.entity.AgentReview;
import com.navisa.be.agent.model.entity.Badge;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.agent.repository.AgentReviewRepository;
import com.navisa.be.agent.repository.BadgeRepository;
import com.navisa.be.application.model.entity.ApplicationForm;
import com.navisa.be.application.service.ApplicationFormCrudService;
import com.navisa.be.chat.model.entity.Proposal;
import com.navisa.be.chat.service.ProposalCrudService;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.service.ForeignerProfileCrudService;
import com.navisa.be.global.common.model.enums.ImageSize;
import com.navisa.be.global.common.service.StorageService;
import com.navisa.be.global.web.response.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class AgentReviewService {

    private final AgentReviewRepository agentReviewRepository;
    private final AgentProfileRepository agentProfileRepository;
    private final StorageService storageService;
    private final ForeignerProfileCrudService foreignerProfileCrudService;
    private final AgentReviewCrudService agentReviewCrudService;
    private final ApplicationEventPublisher eventPublisher;
    private final BadgeRepository badgeRepository;
    private final ReviewReliabilityService reviewReliabilityService;
    private final ApplicationFormCrudService applicationFormCrudService;
    private final ProposalCrudService proposalCrudService;

    // 행정사 후기 사례 최신순 3개 조회
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getLatestFeedbacks() {
        List<AgentReview> reviews = agentReviewRepository.findTop3ValidFeedbacks(PageRequest.of(0, 3));

        if (reviews.isEmpty()) {
            throw new AgentException(ResponseStatus.AGENT_REVIEW_NOT_FOUND);
        }

        List<UUID> profileIds = reviews.stream()
                .map(AgentReview::getAgentProfileId)
                .distinct()
                .toList();

        Map<UUID, AgentProfile> profileMap = agentProfileRepository.findAllById(profileIds)
                .stream()
                .collect(Collectors.toMap(AgentProfile::getId, Function.identity()));

        return reviews.stream()
                .map(review -> {
                    AgentProfile profile = profileMap.get(review.getAgentProfileId());

                    if (profile == null) {
                        throw new AgentException(ResponseStatus.REVIEWED_AGENT_NOT_FOUND);
                    }

                    String profileUrl = storageService.getImgUrl(ImageSize.SMALL, profile.getProfileObjectKey(), false);

                    return new FeedbackResponse(
                            review.getId(),
                            review.getFeedbackContent(),
                            profile.getId(),
                            profile.getName(),
                            profileUrl
                    );
                })
                .toList();
    }

    public void registerAgentReview(String loginUserEmail, CreateAgentReviewRequest request) {
        ForeignerProfile foreignerProfile = foreignerProfileCrudService.findByEmail(loginUserEmail);

        Proposal proposal = proposalCrudService.findLatestProposalByAgentIdAndForeignerId(request.agentId(), foreignerProfile.getId());

        validateProposal(proposal);
        validateApplicationForm(request, foreignerProfile);

        // 뱃지가 모두 존재하는지 확인
        Set<Long> requestedIds = new HashSet<>(request.badgeIdList());
        List<Badge> badges = badgeRepository.findAllByIdIn(requestedIds.stream().toList());
        validateBadges(badges, requestedIds);

        agentReviewCrudService.createAgentReview(request.agentId(), foreignerProfile.getId(), proposal.getId(), badges);

        // 뱃지 summary 업데이트를 위한 이벤트 발행
        ReviewCreatedBadgeEvent reviewCreatedBadgeEvent = new ReviewCreatedBadgeEvent(
                request.agentId(),
                badges.stream().map(Badge::getId).toList()
        );
        eventPublisher.publishEvent(reviewCreatedBadgeEvent);

        // Step 4-1: 행정사 특화 분야 추천을 위한 상대 신뢰도(ria) 계산
        ReviewReliabilityResponse result = reviewReliabilityService.getReviewReliability(foreignerProfile);

        // Step 4-2 처리를 위한 이벤트 발행 (ria 리스트 포함)
        ReviewCreatedSpecializedJobEvent reviewCreatedSpecializedJobEvent = new ReviewCreatedSpecializedJobEvent(
                request.agentId(),
                result.top3JobIds(),
                result.relativeRatios()
        );
        eventPublisher.publishEvent(reviewCreatedSpecializedJobEvent);
    }

    private void validateProposal(Proposal proposal) {
        // 리뷰가 존재하는지 여부를 확인
        if (agentReviewCrudService.existsByProposalId(proposal.getId())) {
            throw new AgentException(ResponseStatus.REVIEW_ALREADY_EXISTS);
        }
    }

    private void validateApplicationForm(CreateAgentReviewRequest request, ForeignerProfile foreignerProfile) {
        // 첫번째 내보내기 여부 확인
        ApplicationForm form = applicationFormCrudService.findCurrentApplicationForm(foreignerProfile.getId(), request.agentId());

        if (!form.isDone()) {
            throw new AgentException(ResponseStatus.NOT_ALLOWED_TO_REVIEW);
        }
    }

    private void validateBadges(List<Badge> badges, Set<Long> requestedIds) {
        if (badges.size() < requestedIds.size()) {
            throw new AgentException(ResponseStatus.BADGE_NOT_FOUND);
        }
    }

    public void createAgentFeedback(String email, String content) {
        ForeignerProfile foreignerProfile = foreignerProfileCrudService.findByEmail(email);

        Proposal proposal = proposalCrudService.findOngoingOneByForeignerId(foreignerProfile.getId());

        agentReviewCrudService.updateAgentFeedback(proposal.getId(), content);
    }
}

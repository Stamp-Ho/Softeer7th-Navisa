package com.navisa.be.foreigner.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.model.entity.AgentReview;
import com.navisa.be.agent.service.AgentProfileCrudService;
import com.navisa.be.agent.service.AgentReviewCrudService;
import com.navisa.be.application.service.ApplicationFormCrudService;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.entity.Proposal;
import com.navisa.be.chat.service.ChatRoomSearchService;
import com.navisa.be.chat.service.ProposalCrudService;
import com.navisa.be.foreigner.dto.request.ForeignerDetailRequest;
import com.navisa.be.foreigner.dto.response.ForeignerDetailResponse;
import com.navisa.be.foreigner.dto.response.ForeignerProgressResponse;
import com.navisa.be.foreigner.dto.response.ForeignerQueryResponse;
import com.navisa.be.foreigner.dto.response.ForeignerStatusResponse;
import com.navisa.be.foreigner.exception.ForeignerException;
import com.navisa.be.foreigner.model.entity.*;
import com.navisa.be.foreigner.repository.ForeignerCareersRepository;
import com.navisa.be.foreigner.repository.ForeignerEducationRepository;
import com.navisa.be.foreigner.repository.ForeignerExpectedCompanyRepository;
import com.navisa.be.foreigner.repository.ForeignerProfileRepository;
import com.navisa.be.global.common.model.entity.Nationality;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.service.UserCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ForeignerProfileDetailService {

    private final UserCrudService userCrudService;
    private final AgentProfileCrudService agentProfileCrudService;
    private final AgentReviewCrudService agentReviewCrudService;
    private final ChatRoomSearchService chatRoomSearchService;
    private final ForeignerProfileRepository foreignerProfileRepository;
    private final ForeignerEducationRepository foreignerEducationRepository;
    private final ForeignerCareersRepository foreignerCareersRepository;
    private final ForeignerExpectedCompanyRepository foreignerExpectedCompanyRepository;
    private final ApplicationFormCrudService applicationFormCrudService;
    private final ProposalCrudService proposalCrudService;

    @Transactional(readOnly = true)
    public ForeignerQueryResponse findForeignerTotalInfo(String email) {
        User user = userCrudService.findByEmail(email);
        ForeignerProfile profile = foreignerProfileRepository.findByUserIdWithNationalitiesAndLanguages(user.getId())
                .orElseThrow(() -> new ForeignerException(ResponseStatus.INVALID_FOREIGNER));

        ForeignerEducation education = foreignerEducationRepository.findByForeignerId(profile.getId())
                .orElseThrow(() -> new ForeignerException(ResponseStatus.INVALID_FOREIGNER));

        List<ForeignerCareers> careers = foreignerCareersRepository.findAllByForeignerId(profile.getId());

        ForeignerExpectedCompany expectedCompany = foreignerExpectedCompanyRepository.findByForeignerId(profile.getId())
                .orElseThrow(() -> new ForeignerException(ResponseStatus.INVALID_FOREIGNER));

        return ForeignerQueryResponse.of(profile, education, careers, expectedCompany);
    }

    @Transactional(readOnly = true)
    public ForeignerDetailResponse findForeignerDetail(ForeignerDetailRequest request) {
        ForeignerProfile foreignerProfile = foreignerProfileRepository.findById(request.foreignerId())
                .orElseThrow(() -> new ForeignerException(ResponseStatus.INVALID_FOREIGNER));

        List<Long> nationIds = foreignerProfile.getForeignerNationalities().stream().map(ForeignerNationality::getNationality).map(Nationality::getId).toList();

        User agentUser = userCrudService.findByEmail(request.loginUserEmail());

        AgentProfile agentProfile = agentProfileCrudService.findByUserId(agentUser.getId());

        Optional<ChatRoom> optChatRoom = chatRoomSearchService.findByAgentIdAndForeignerId(agentProfile.getId(), foreignerProfile.getId());

        ForeignerEducation foreignerEducation = foreignerEducationRepository.findByForeignerId(foreignerProfile.getId())
                .orElseThrow(() -> new ForeignerException(ResponseStatus.INVALID_FOREIGNER));

        List<Long> langIdList = foreignerProfile.getForeignLanguages().stream()
                .map(foreignerLanguage -> foreignerLanguage.getLanguage().getId())
                .toList();

        // 외국인의 경력들을 조회
        List<ForeignerCareers> careers = foreignerCareersRepository.findAllByForeignerId(foreignerProfile.getId());

        ForeignerExpectedCompany expectedCompany = foreignerExpectedCompanyRepository.findByForeignerId(foreignerProfile.getId())
                .orElseThrow(() -> new ForeignerException(ResponseStatus.INVALID_FOREIGNER));

        return new ForeignerDetailResponse(
                ForeignerDetailResponse.ForeignerBasicInfo.of(foreignerProfile, nationIds, optChatRoom),
                new ForeignerDetailResponse.EducationInfo(foreignerEducation.getDegreeLevel(), foreignerEducation.getSchoolName(), foreignerEducation.getMajorName()),
                langIdList,
                ForeignerDetailResponse.CareerInfo.of(careers),
                ForeignerDetailResponse.ExpectedCompanyInfo.of(expectedCompany)
        );
    }

    // 외국인 상세 요건 입력 여부 확인
    public ForeignerStatusResponse checkForeignerFilledStatus(String email) {
        User user = userCrudService.findByEmail(email);

        ForeignerProfile profile = foreignerProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ForeignerException(ResponseStatus.INVALID_FOREIGNER));

        boolean isCompletedRecommendation = (user.getUserType() == UserType.FILLED_FOREIGNER);

        return new ForeignerStatusResponse(
                profile.getId(),
                isCompletedRecommendation
        );
    }

    @Transactional(readOnly = true)
    public ForeignerProgressResponse getForeignerProgress(String email) {
        User user = userCrudService.findByEmail(email);

        ForeignerProfile profile = foreignerProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ForeignerException(ResponseStatus.INVALID_FOREIGNER));

        Optional<Proposal> latestProposalOpt = proposalCrudService.findLatestMatchedProposal(profile.getId());

        // 매칭된 제안이 없는 경우 모두 false 반환
        if (latestProposalOpt.isEmpty()) {
            return new ForeignerProgressResponse(false, false, false, false, null);
        }

        Proposal proposal = latestProposalOpt.get();

        UUID matchedAgentId = proposal.getChatRoom().getAgentProfile().getId();

        Optional<AgentReview> reviewOpt = agentReviewCrudService.findOptionalByProposalId(proposal.getId());
        boolean isReview = reviewOpt.isPresent();
        boolean isFeedback = reviewOpt.map(r -> r.getFeedbackContent() != null).orElse(false);
        boolean isFinished = applicationFormCrudService.existsByForeignerProfileIdAndAgentProfileIdAndIsFinishedTrue(profile.getId(), matchedAgentId);

        return new ForeignerProgressResponse(isReview, isFeedback, isFinished, true, proposal.getChatRoom().getId());
    }
}

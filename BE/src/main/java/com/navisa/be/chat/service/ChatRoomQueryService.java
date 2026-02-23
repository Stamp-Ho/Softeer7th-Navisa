package com.navisa.be.chat.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.service.AgentBadgeService;
import com.navisa.be.chat.dto.projection.ChatRoomInfoProjection;
import com.navisa.be.agent.service.AgentReviewCrudService;
import com.navisa.be.application.model.entity.ApplicationForm;
import com.navisa.be.application.service.ApplicationFormCrudService;
import com.navisa.be.chat.dto.response.GetChatRoomParticipantsInfoResponse;
import com.navisa.be.chat.exception.ChatRoomException;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.entity.Proposal;
import com.navisa.be.chat.model.enums.ChatRoomFilterType;
import com.navisa.be.chat.model.enums.ProposalStatus;
import com.navisa.be.chat.repository.ChatRoomRepository;
import com.navisa.be.foreigner.service.ForeignerProfileCrudService;
import com.navisa.be.global.web.request.SliceRequest;
import com.navisa.be.global.common.model.entity.Nationality;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.foreigner.model.entity.ForeignerExpectedCompany;
import com.navisa.be.foreigner.model.entity.ForeignerNationality;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.service.UserCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatRoomQueryService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserCrudService userCrudService;
    private final AgentBadgeService agentBadgeService;
    private final ForeignerProfileCrudService foreignerProfileCrudService;
    private final ApplicationFormCrudService applicationFormCrudService;
    private final ProposalCrudService proposalCrudService;
    private final AgentReviewCrudService agentReviewCrudService;

    public List<ChatRoomInfoProjection> findChatRoomByProfileId(
            UUID foreignerId, SliceRequest<Long> slice, boolean isForeignerId, ChatRoomFilterType filter) {

        return chatRoomRepository.findByNoOffset(foreignerId, slice, isForeignerId, filter);
    }

    public ChatRoom findById(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatRoomException(ResponseStatus.INVALID_CHATROOM));
    }

    public ChatRoom findByIdWithProfiles(Long roomId) {
        return chatRoomRepository.findByIdWithProfiles(roomId)
                .orElseThrow(() -> new ChatRoomException(ResponseStatus.INVALID_CHATROOM));
    }

    public ChatRoom findByIdWithLock(Long roomId) {
        return chatRoomRepository.findByIdWithLock(roomId)
                .orElseThrow(() -> new ChatRoomException(ResponseStatus.INVALID_CHATROOM));
    }

    public boolean isOwnedByProfileIdAndChatRoomId(Long roomId, ForeignerProfile foreignerProfile) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatRoomException(ResponseStatus.INVALID_CHATROOM))
                .getForeignerProfile().getId().equals(foreignerProfile.getId());
    }

    public boolean isOwnedByProfileIdAndChatRoomId(Long roomId, AgentProfile agentProfile) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatRoomException(ResponseStatus.INVALID_CHATROOM))
                .getAgentProfile().getId().equals(agentProfile.getId());
    }

    public boolean existsByAgentIdAndForeignerId(UUID agentId, UUID foreignerId) {
        return chatRoomRepository.existsByAgentProfileIdAndForeignerProfileId(agentId, foreignerId);
    }

    @Transactional(readOnly = true)
    public GetChatRoomParticipantsInfoResponse findParticipantsInfoById(Long roomId, String loginUserEmail) {
        User loginUser = userCrudService.findByEmail(loginUserEmail);

        ChatRoom chatRoom = chatRoomRepository.findByIdWithParticipantsInfo(roomId)
                .orElseThrow(() -> new ChatRoomException(ResponseStatus.NOT_FOUND_CHATROOM));

        // 요청자가 대화 참여자가 아니면 예외
        if (!(chatRoom.getAgentProfile().getUserId().equals(loginUser.getId())
                || chatRoom.getForeignerProfile().getUserId().equals(loginUser.getId()))) {
            throw new ChatRoomException(ResponseStatus.NOT_ALLOWED_TO_ACCESS_CHATROOM);
        }

        ForeignerProfile foreignerProfile = chatRoom.getForeignerProfile();
        ForeignerExpectedCompany expectedCompany = foreignerProfileCrudService
                .findExpectedCompanyByForeignerProfileId(foreignerProfile.getId());
        List<Long> nationalityIds = foreignerProfile.getForeignerNationalities().stream()
                .map(ForeignerNationality::getNationality)
                .map(Nationality::getId)
                .toList();

        AgentProfile agentProfile = chatRoom.getAgentProfile();
        List<Long> top2BadgeIds = agentBadgeService.getTop2BadgeIds(agentProfile.getId());

        Optional<ApplicationForm> form = applicationFormCrudService
                .findOptionalCurrentApplicationForm(foreignerProfile.getId(), agentProfile.getId());
        Optional<Proposal> proposal = proposalCrudService.findOptionalLatestProposalByChatRoom(chatRoom);

        boolean isReviewRequired = false;
        boolean proposalEndRequired = false;
        UUID applicationFormId = null;

        if (proposal.isPresent() && form.isPresent()) {
            boolean reviewExists = agentReviewCrudService.existsByProposalId(proposal.get().getId());
            isReviewRequired = form.get().isDone() && !reviewExists;
            proposalEndRequired = form.get().requiresEnd();

            if (proposal.get().getStatus() == ProposalStatus.MATCHED) {
                applicationFormId = form.get().getId();
            }
        }

        return GetChatRoomParticipantsInfoResponse.entityToDto(
                agentProfile,
                top2BadgeIds,
                foreignerProfile,
                expectedCompany,
                nationalityIds,
                isReviewRequired,
                applicationFormId,
                proposalEndRequired);
    }

    public Optional<ChatRoom> findByAgentIdAndForeignerId(UUID agentId, UUID foreignerId) {
        return chatRoomRepository.findByAgentIdAndForeignerId(agentId, foreignerId);
    }

    public Optional<ChatRoom> findOptionalByAgentProfileAndForeignerProfile(AgentProfile agent,
            ForeignerProfile foreigner) {
        if (agent == null || foreigner == null) {
            return Optional.empty();
        }
        return chatRoomRepository.findByAgentProfileAndForeignerProfile(agent, foreigner);
    }

    public Long getChatRoomIdByProfiles(AgentProfile agent, ForeignerProfile foreigner) {
        return findOptionalByAgentProfileAndForeignerProfile(agent, foreigner)
                .map(ChatRoom::getId)
                .orElse(null);
    }

    public ChatRoom findByAgentProfileAndForeignerProfile(AgentProfile agent, ForeignerProfile foreigner) {
        return chatRoomRepository.findByAgentProfileAndForeignerProfile(agent, foreigner)
                .orElseThrow(() -> new ChatRoomException(ResponseStatus.NOT_FOUND_CHATROOM));
    }
}

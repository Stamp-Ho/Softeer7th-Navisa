package com.navisa.be.chat.service;

import com.navisa.be.agent.service.AgentProfileCrudService;
import com.navisa.be.application.model.entity.ApplicationForm;
import com.navisa.be.application.service.ApplicationFormCrudService;
import com.navisa.be.chat.dto.message.ChatMessageRequest;
import com.navisa.be.chat.dto.projection.ChatRoomProposalStatusProjection;
import com.navisa.be.chat.exception.ChatRoomException;
import com.navisa.be.chat.exception.ProposalException;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.entity.Proposal;
import com.navisa.be.chat.model.enums.ProposalStatus;
import com.navisa.be.chat.repository.ProposalRepository;
import com.navisa.be.foreigner.service.ForeignerProfileCrudService;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.service.UserCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

@RequiredArgsConstructor
@Service
public class ProposalService {

    private final ProposalRepository proposalRepository;
    private final UserCrudService userCrudService;
    private final ForeignerProfileCrudService foreignerProfileCrudService;
    private final AgentProfileCrudService agentProfileQueryService;
    private final ChatRoomQueryService chatRoomQueryService;
    private final ChatServiceFacade chatServiceFacade;
    private final ApplicationFormCrudService applicationFormCrudService;

    @Transactional(readOnly = true)
    public List<ChatRoomProposalStatusProjection> findByChatRoomIn(Collection<ChatRoom> contentChatRooms) {
        return proposalRepository.findProposalStatusByChatRoomIn(contentChatRooms);
    }

    @Transactional
    public void createProposal(String email, Long roomId, ChatMessageRequest request) {
        validateRoomId(roomId, request);
        executeProposalAction(email, roomId, request, this::createWithValidation);
    }

    @Transactional
    public void updateProposalStatusMatched(String email, Long roomId, ChatMessageRequest request) {
        validateRoomId(roomId, request);
        executeProposalAction(email, roomId, request,
                (room, req) -> updateStatusByChatRoom(room, ProposalStatus.MATCHED, req));
    }

    @Transactional
    public void updateProposalStatusRejected(String email, Long roomId, ChatMessageRequest request) {
        validateRoomId(roomId, request);
        executeProposalAction(email, roomId, request,
                (room, req) -> updateStatusByChatRoom(room, ProposalStatus.REJECTED, req));
    }

    @Transactional
    public void updateProposalStatusCanceled(String email, Long roomId, ChatMessageRequest request) {
        validateRoomId(roomId, request);
        executeProposalAction(email, roomId, request,
                (room, req) -> updateStatusByChatRoom(room, ProposalStatus.CANCELED, req));
    }

    private void validateRoomId(Long roomId, ChatMessageRequest request) {
        if (!roomId.equals(request.roomId())) {
            throw new ProposalException(ResponseStatus.BAD_REQUEST, "요청한 방 ID가 일치하지 않습니다.");
        }
    }

    @Transactional
    protected void executeProposalAction(String email, Long roomId, ChatMessageRequest request,
                                         BiFunction<ChatRoom, ChatMessageRequest, ChatMessageRequest> dbAction) {
        User user = userCrudService.findByEmail(email);
        UUID profileId = getProfileId(user);
        ChatRoom room = chatRoomQueryService.findByIdWithProfiles(roomId);

        validateChatRoomOwnership(profileId, user.getUserType(), room);

        ChatMessageRequest finalMessageRequest = dbAction.apply(room, request);

        chatServiceFacade.saveAndPublishChatMessage(user.getId(), finalMessageRequest, room);
    }

    @Transactional
    public ChatMessageRequest createWithValidation(ChatRoom room, ChatMessageRequest request) {
        Proposal proposal = proposalRepository.findFirstByChatRoom_IdOrderByIdDesc(room.getId())
                .orElse(null);

        if (proposal != null && proposal.getStatus().equals(ProposalStatus.PROPOSED)) {
            throw new ProposalException(ResponseStatus.PROPOSAL_ALREADY_EXISTS, "해당 채팅방에 PROPOSED 상태인 제안이 이미 존재합니다.");
        }

        proposalRepository.save(new Proposal(room, room.getAgentProfile().getId()));
        return request;
    }

    @Transactional
    public ChatMessageRequest updateStatusByChatRoom(ChatRoom chatRoom, ProposalStatus updatedStatus, ChatMessageRequest request) {
        Proposal proposal = proposalRepository.findFirstByChatRoom_IdOrderByIdDesc(chatRoom.getId())
                .orElseThrow(() -> new ProposalException(ResponseStatus.BAD_REQUEST, "현재 진행 중인 제안이 없습니다."));

        // 상태 변경 가능 여부 검증 (REJECTED/CANCELED 상태면 변경 불가)
        if (proposal.getStatus().equals(ProposalStatus.REJECTED)
                || proposal.getStatus().equals(ProposalStatus.CANCELED)
                || proposal.getStatus().equals(ProposalStatus.COMPLETED)) {
            throw new ProposalException(ResponseStatus.BAD_REQUEST, "이미 완료된 제안 상태입니다.");
        }

        validateStatusTransition(proposal.getStatus(), updatedStatus);

        proposal.updateStatus(updatedStatus);
        ChatMessageRequest finalMessageRequest = request;

        if (updatedStatus.equals(ProposalStatus.MATCHED)) {
            ApplicationForm form = applicationFormCrudService.findRecentApplicationFormByForeignerId(
                    chatRoom.getForeignerProfile().getId());

            form.updateAgentProfile(chatRoom.getAgentProfile());
            finalMessageRequest = request.updateContent(form.getId().toString());
        }

        if (updatedStatus.equals(ProposalStatus.CANCELED)) {
            ApplicationForm form = applicationFormCrudService.findRecentApplicationFormByForeignerId(
                    chatRoom.getForeignerProfile().getId());
            form.updateAgentProfile(null);
        }

        proposalRepository.save(proposal);
        return finalMessageRequest;
    }

    private void validateStatusTransition(ProposalStatus current, ProposalStatus target) {
        if ((target == ProposalStatus.MATCHED || target == ProposalStatus.REJECTED)
                && current != ProposalStatus.PROPOSED) {
            throw new ProposalException(ResponseStatus.BAD_REQUEST, "PROPOSED 상태에서만 승인/거절이 가능합니다.");
        }

        if (target == ProposalStatus.CANCELED && current != ProposalStatus.MATCHED) {
            throw new ProposalException(ResponseStatus.BAD_REQUEST, "MATCHED 상태에서만 취소가 가능합니다.");
        }
    }

    private UUID getProfileId(User user) {
        return user.getUserType().equals(UserType.FILLED_FOREIGNER)
                ? foreignerProfileCrudService.findByUserId(user.getId()).getId()
                : agentProfileQueryService.findByUserId(user.getId()).getId();
    }

    private void validateChatRoomOwnership(UUID profileId, UserType userType, ChatRoom room) {
        if (userType.equals(UserType.FILLED_FOREIGNER)) {
            if (!room.getForeignerProfile().getId().equals(profileId)) {
                throw new ChatRoomException(ResponseStatus.NOT_ALLOWED_TO_ACCESS_CHATROOM);
            }
            return;
        }

        if (!room.getAgentProfile().getId().equals(profileId)) {
            throw new ChatRoomException(ResponseStatus.NOT_ALLOWED_TO_ACCESS_CHATROOM);
        }
    }

    @Transactional
    public void updateProposalOnBlock(ChatRoom chatRoom) {
        Proposal proposal = proposalRepository.findFirstByChatRoom_IdOrderByIdDesc(chatRoom.getId())
                .orElse(null);

        // 제안이 없으면 아무것도 하지 않음
        if (proposal == null) {
            return;
        }

        ProposalStatus currentStatus = proposal.getStatus();

        // 이미 완료된 상태면 아무것도 하지 않음
        if (currentStatus == ProposalStatus.REJECTED
                || currentStatus == ProposalStatus.CANCELED
                || currentStatus == ProposalStatus.COMPLETED) {
            return;
        }

        // PROPOSED → REJECTED
        if (currentStatus == ProposalStatus.PROPOSED) {
            proposal.updateStatus(ProposalStatus.REJECTED);
            proposalRepository.save(proposal);
        }
        // MATCHED → CANCELED
        else if (currentStatus == ProposalStatus.MATCHED) {
            proposal.updateStatus(ProposalStatus.CANCELED);
            proposalRepository.save(proposal);
        }
        ApplicationForm form = applicationFormCrudService.findRecentApplicationFormByForeignerId(
                chatRoom.getForeignerProfile().getId());
        form.updateAgentProfile(null);
    }
}

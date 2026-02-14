package com.navisa.be.chat.service;

import com.navisa.be.agent.service.AgentProfileQueryService;
import com.navisa.be.chat.dto.message.ChatMessageRequest;
import com.navisa.be.chat.dto.projection.ChatRoomProposalStatusProjection;
import com.navisa.be.chat.exception.ChatRoomException;
import com.navisa.be.chat.exception.ProposalException;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.entity.Proposal;
import com.navisa.be.chat.model.enums.ProposalStatus;
import com.navisa.be.chat.repository.ProposalRepository;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.foreigner.service.ForeignerQueryService;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

@RequiredArgsConstructor
@Service
public class ProposalService {

    private final ProposalRepository proposalRepository;
    private final UserQueryService userQueryService;
    private final ForeignerQueryService foreignerQueryService;
    private final AgentProfileQueryService agentProfileQueryService;
    private final ChatRoomQueryService chatRoomQueryService;
    private final ChatServiceFacade chatServiceFacade;

    @Transactional(readOnly = true)
    public List<ChatRoomProposalStatusProjection> findByChatRoomIn(Collection<ChatRoom> contentChatRooms) {
        return proposalRepository.findProposalStatusByChatRoomIn(contentChatRooms);
    }

    @Transactional
    public void createProposal(String email, Long roomId, ChatMessageRequest request) {
        if (!roomId.equals(request.roomId())) {
            throw new ProposalException(ResponseStatus.BAD_REQUEST);
        }
        executeProposalAction(email, roomId, request, this::createWithValidation);
    }

    @Transactional
    public void updateProposalStatusMatched(String email, Long roomId, ChatMessageRequest request) {
        if (!roomId.equals(request.roomId())) {
            throw new ProposalException(ResponseStatus.BAD_REQUEST);
        }
        executeProposalAction(email, roomId, request,
                (room, profileId) -> updateStatusByChatRoomId(room.getId(), ProposalStatus.MATCHED));
    }

    @Transactional
    public void updateProposalStatusRejected(String email, Long roomId, ChatMessageRequest request) {
        if (!roomId.equals(request.roomId())) {
            throw new ProposalException(ResponseStatus.BAD_REQUEST);
        }
        executeProposalAction(email, roomId, request,
                (room, profileId) -> updateStatusByChatRoomId(room.getId(), ProposalStatus.REJECTED));
    }

    @Transactional
    public void updateProposalStatusCanceled(String email, Long roomId, ChatMessageRequest request) {
        if (!roomId.equals(request.roomId())) {
            throw new ProposalException(ResponseStatus.BAD_REQUEST);
        }
        executeProposalAction(email, roomId, request,
                (room, profileId) -> updateStatusByChatRoomId(room.getId(), ProposalStatus.CANCELED));
    }

    @Transactional
    protected void executeProposalAction(String email, Long roomId, ChatMessageRequest request, BiConsumer<ChatRoom, UUID> dbAction) {
        User user = userQueryService.findByEmail(email);

        UUID profileId = getProfileId(user);

        ChatRoom room = chatRoomQueryService.findByIdWithProfiles(roomId);

        validateChatRoomOwnership(profileId, user.getUserType(), room);

        // DB 작업 수행 (트랜잭션 분리를 위해 별도 메서드 호출이나 내부 로직 수행)
        dbAction.accept(room, profileId);

        // Redis Pub/Sub 발행 (DB 트랜잭션과 분리되어 실행됨)
        chatServiceFacade.saveAndPublishChatMessage(user.getId(), request, room);
    }

    private UUID getProfileId(User user) {
        return user.getUserType().equals(UserType.FILLED_FOREIGNER)
                ? foreignerQueryService.findByUserId(user.getId()).getId()
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
    public void createWithValidation(ChatRoom room, UUID profileId) {
        Proposal proposal = proposalRepository.findFirstByChatRoom_IdOrderByIdDesc(room.getId())
                .orElse(null);

        if (proposal != null && proposal.getStatus().equals(ProposalStatus.PROPOSED)) {
            throw new ProposalException(ResponseStatus.PROPOSAL_ALREADY_EXISTS, "해당 채팅방에 PROPOSED 상태인 제안이 이미 존재합니다.");
        }

        proposalRepository.save(new Proposal(room, profileId));
    }

    @Transactional
    public void updateStatusByChatRoomId(Long id, ProposalStatus updatedStatus) {
        Proposal proposal = proposalRepository.findFirstByChatRoom_IdOrderByIdDesc(id).orElse(null);

        if (proposal == null) { // 다른 호출로직에서는 만약 제안이 없다면 예외를 터뜨리지 말아야 함
            return;
        }

        if ((updatedStatus.equals(ProposalStatus.MATCHED)
                || updatedStatus.equals(ProposalStatus.REJECTED))
            && !proposal.getStatus().equals(ProposalStatus.PROPOSED)
        ) {
            throw new ProposalException(ResponseStatus.BAD_REQUEST,
                    "PROPOSED가 아닌 제안은 MATCHED, REJECTED로 변경하지 못합니다.");
        }

        if (updatedStatus.equals(ProposalStatus.CANCELED) && !proposal.getStatus().equals(ProposalStatus.MATCHED)) {
            throw new ProposalException(ResponseStatus.BAD_REQUEST,
                    "MATCHED가 아닌 제안은 CANCELED로 변경하지 못합니다.");
        }

        proposal.updateStatus(updatedStatus);
        proposalRepository.save(proposal);
    }
}

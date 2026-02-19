package com.navisa.be.chat.service;

import com.navisa.be.agent.service.AgentProfileCrudService;
import com.navisa.be.chat.dto.message.ChatMessageRequest;
import com.navisa.be.chat.dto.projection.ChatRoomProposalStatusProjection;
import com.navisa.be.chat.exception.ChatRoomException;
import com.navisa.be.chat.exception.ProposalException;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.entity.Proposal;
import com.navisa.be.chat.model.enums.ProposalStatus;
import com.navisa.be.chat.repository.ChatRoomRepository;
import com.navisa.be.chat.repository.ProposalRepository;
import com.navisa.be.foreigner.service.ForeignerProfileCrudService;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.service.UserCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

@RequiredArgsConstructor
@Service
public class ProposalService {

    private final ProposalRepository proposalRepository;
    private final UserCrudService userCrudService;
    private final ForeignerProfileCrudService foreignerProfileCrudService;
    private final AgentProfileCrudService agentProfileQueryService;
    private final ChatRoomQueryService chatRoomQueryService;
    private final ChatServiceFacade chatServiceFacade;
    private final ChatRoomRepository chatRoomRepository;

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
        User user = userCrudService.findByEmail(email);

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

        if (proposal == null // 만약 제안이 없거나 거절/취소로 완료된 상태에서 변경 시도를 할 경우 예외를 터뜨림
                || proposal.getStatus().equals(ProposalStatus.REJECTED)
                || proposal.getStatus().equals(ProposalStatus.CANCELED)) {
            throw new ProposalException(ResponseStatus.BAD_REQUEST, "현재 제안이 없거나 거절/취소로 완료된 상태입니다.");
        }

        if ((updatedStatus.equals(ProposalStatus.MATCHED)
                || updatedStatus.equals(ProposalStatus.REJECTED))
                && !proposal.getStatus().equals(ProposalStatus.PROPOSED)) {
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

    /**
     * 채팅방 차단 시 제안 상태를 변경합니다.
     * - PROPOSED 상태 → REJECTED로 변경
     * - MATCHED 상태 → CANCELED로 변경
     * - 제안이 없거나 이미 REJECTED/CANCELED 상태인 경우 아무 작업도 하지 않습니다.
     */
    @Transactional
    public void updateProposalOnBlock(Long chatRoomId) {
        Proposal proposal = proposalRepository.findFirstByChatRoom_IdOrderByIdDesc(chatRoomId)
                .orElse(null);

        // 제안이 없으면 아무것도 하지 않음
        if (proposal == null) {
            return;
        }

        ProposalStatus currentStatus = proposal.getStatus();

        // 이미 완료된 상태면 아무것도 하지 않음
        if (currentStatus == ProposalStatus.REJECTED || currentStatus == ProposalStatus.CANCELED) {
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
    }

    @Transactional(readOnly = true)
    public Proposal findLatestProposalByAgentIdAndForeignerId(UUID agentId, UUID foreignerId) {
        ChatRoom chatRoom = chatRoomRepository.findByAgentIdAndForeignerId(agentId, foreignerId)
                .orElseThrow(() -> new ProposalException(ResponseStatus.NOT_FOUND_CHATROOM));

        Proposal proposal = proposalRepository.findFirstByChatRoomOrderByIdDesc(chatRoom)
                .orElseThrow(() -> new ProposalException(ResponseStatus.PROPOSAL_NOT_FOUND));

        return proposal;
    }

    public Proposal findOngoingOneByForeignerId(UUID foreignerId) {
        // TODO :: 외국인이 리뷰를 남겨야할 수임 제안을 찾을 때, matched나 completed만으로 해도 되나? 특정 행정사에 대해서 matched나 completed를 찾아야 하지 않나?

        List<Proposal> proposals = proposalRepository.findLatestMatchedProposal(
                foreignerId,
                List.of(ProposalStatus.MATCHED, ProposalStatus.COMPLETED),
                PageRequest.of(0, 1)
        );

        Proposal proposal = proposals.stream()
                .findFirst()
                .orElseThrow(() -> new ProposalException(ResponseStatus.PROPOSAL_NOT_FOUND));

        return proposal;
    }

    @Transactional(readOnly = true)
    public Optional<Proposal> findLatestMatchedProposal(UUID foreignerId) {
        List<ProposalStatus> targetStatuses = List.of(ProposalStatus.MATCHED, ProposalStatus.COMPLETED);

        List<Proposal> proposals = proposalRepository.findLatestMatchedProposal(
                foreignerId,
                targetStatuses,
                PageRequest.of(0, 1)
        );

        return proposals.stream().findFirst();
    }

    public Optional<Proposal> findFirstByChatRoomOrderByIdDesc(ChatRoom chatRoom) {
        return proposalRepository.findFirstByChatRoomOrderByIdDesc(chatRoom);
    }
}

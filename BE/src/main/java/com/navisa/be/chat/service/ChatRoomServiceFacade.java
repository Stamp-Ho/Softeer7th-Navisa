package com.navisa.be.chat.service;

import com.navisa.be.agent.service.AgentProfileCrudService;
import com.navisa.be.application.service.ApplicationCommandService;
import com.navisa.be.chat.dto.projection.ChatMessageNonReadCountProjection;
import com.navisa.be.chat.dto.projection.ChatRoomProposalStatusProjection;
import com.navisa.be.chat.dto.response.ChatRoomCardResponse;
import com.navisa.be.chat.exception.ChatRoomException;
import com.navisa.be.chat.model.entity.ChatMessage;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ChatRoomFilterType;
import com.navisa.be.chat.model.enums.ProposalStatus;
import com.navisa.be.foreigner.service.ForeignerProfileCrudService;
import com.navisa.be.global.common.service.StorageService;
import com.navisa.be.global.web.request.SliceRequest;
import com.navisa.be.global.web.response.SliceResponse;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.global.common.model.enums.ImageSize;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceFacade {

    private final UserQueryService userQueryService;
    private final ChatRoomQueryService chatRoomQueryService;
    private final ForeignerProfileCrudService foreignerProfileCrudService;
    private final AgentProfileCrudService agentProfileCrudService;
    private final ChatMessageQueryService chatMessageQueryService;
    private final ProposalService proposalService;
    private final ApplicationCommandService applicationCommandService;
    private final ChatRoomCommandService chatRoomCommandService;
    private final StorageService storageService;

    public SliceResponse<ChatRoomCardResponse, Long> findAllChatRoomsByNoOffset(String email, String filter,
            SliceRequest<Long> slice) {
        ChatRoomFilterType filterType = ChatRoomFilterType.from(filter);

        User user = userQueryService.findByEmail(email);
        boolean isForeigner = user.getUserType().equals(UserType.FILLED_FOREIGNER);

        // 1. 자신의 프로필 ID 조회
        UUID profileId = isForeigner
                ? foreignerProfileCrudService.findByUserId(user.getId()).getId()
                : agentProfileCrudService.findByUserId(user.getId()).getId();

        // 2. 채팅방 목록 조회 (ExistsNext 확인을 위해 Repository에서 slice.size() + 1개를 가져와야 함)
        List<ChatRoom> chatRoomList = chatRoomQueryService.findChatRoomByProfileId(profileId, slice, isForeigner,
                filterType);

        if (chatRoomList.isEmpty())
            return new SliceResponse<>(List.of(), false, null);

        boolean existsNext = chatRoomList.size() > slice.size();
        List<ChatRoom> contentChatRooms = existsNext ? chatRoomList.subList(0, slice.size()) : chatRoomList;

        List<Long> contentChatRoomIds = contentChatRooms.stream().map(ChatRoom::getId).toList();

        Map<Long, String> lastMessageMap = chatMessageQueryService
                .findAllLastChatMessageByChatRoomIn(contentChatRoomIds)
                .stream()
                .collect(Collectors.toMap(
                        message -> message.getChatRoom().getId(),
                        ChatMessage::getContent,
                        (existing, replacement) -> existing));

        boolean allMatched = contentChatRooms.stream()
                .map(ChatRoom::getId)
                .allMatch(lastMessageMap::containsKey);

        if (!allMatched) {
            throw new ChatRoomException(ResponseStatus.INVALID_CHATMESSAGE);
        }

        Map<Long, Long> nonReadCountMap = chatMessageQueryService.findCountByChatRoomIn(contentChatRooms, profileId)
                .stream()
                .collect(Collectors.toMap(
                        ChatMessageNonReadCountProjection::getId,
                        ChatMessageNonReadCountProjection::getCount,
                        (existing, replacement) -> existing));

        Map<Long, ChatRoomProposalStatusProjection> proposalStatusMap = proposalService
                .findByChatRoomIn(contentChatRooms)
                .stream()
                .collect(Collectors.toMap(
                        ChatRoomProposalStatusProjection::getChatRoomId,
                        Function.identity(),
                        (existing, replacement) -> existing));

        // 3. 응답 DTO 변환 (상대방 프로필 정보 매핑)
        List<ChatRoomCardResponse> responses = contentChatRooms.stream()
                .map(chatRoom -> ChatRoomCardResponse.toDto(
                        chatRoom,
                        getProfileImgUrl(chatRoom, isForeigner),
                        lastMessageMap.get(chatRoom.getId()),
                        nonReadCountMap.getOrDefault(chatRoom.getId(), 0L),
                        isForeigner,
                        hasReceivedProposal(chatRoom, profileId, proposalStatusMap),
                        isProposalMatched(chatRoom, proposalStatusMap)))
                .toList();

        Long lastElementId = contentChatRooms.isEmpty() ? null
                : contentChatRooms.get(contentChatRooms.size() - 1).getId();

        return new SliceResponse<>(responses, existsNext, lastElementId);
    }

    private String getProfileImgUrl(ChatRoom chatRoom, boolean isForeigner) {
        if (isForeigner) {
            return storageService.getImgUrl(
                    ImageSize.MEDIUM, chatRoom.getAgentProfile().getProfileObjectKey(), false);
        }
        return null;
    }

    private boolean hasReceivedProposal(ChatRoom chatRoom, UUID profileId,
            Map<Long, ChatRoomProposalStatusProjection> proposalStatusMap) {
        if (!proposalStatusMap.containsKey(chatRoom.getId())) {
            return false;
        }
        ChatRoomProposalStatusProjection projection = proposalStatusMap.get(chatRoom.getId());
        // 내가 보낸 게 아닌 제안이 생성된 상태면 true
        return !projection.getSenderId().equals(profileId) && projection.getStatus() == ProposalStatus.PROPOSED;
    }

    private boolean isProposalMatched(ChatRoom chatRoom,
            Map<Long, ChatRoomProposalStatusProjection> proposalStatusMap) {
        return proposalStatusMap.containsKey(chatRoom.getId())
                && proposalStatusMap.get(chatRoom.getId()).getStatus() == ProposalStatus.MATCHED;
    }

    @Transactional
    public void updateBlockStatusToEntity(String email, Long chatRoomId) {
        User user = userQueryService.findByEmail(email);

        ChatRoom chatRoom = chatRoomQueryService.findByIdWithProfiles(chatRoomId);

        validateChatRoomOwnership(user, chatRoom);

        chatRoomCommandService.updateStatus(chatRoom);
        proposalService.updateProposalOnBlock(chatRoom.getId());
        applicationCommandService.updateAgentProfileConnection(chatRoom);
    }

    private void validateChatRoomOwnership(User user, ChatRoom chatRoom) {
        UUID profileId = user.getUserType().equals(UserType.FILLED_FOREIGNER)
                ? foreignerProfileCrudService.findByUserId(user.getId()).getId()
                : agentProfileCrudService.findByUserId(user.getId()).getId(); // 프로필 ID를 먼저 추출

        boolean isParticipant = user.getUserType().equals(UserType.FILLED_FOREIGNER)
                ? chatRoom.getForeignerProfile().getId().equals(profileId)
                : chatRoom.getAgentProfile().getId().equals(profileId);

        if (!isParticipant) {
            throw new ChatRoomException(ResponseStatus.NOT_ALLOWED_TO_ACCESS_CHATROOM);
        }
    }
}

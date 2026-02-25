package com.navisa.be.chat.service;

import com.navisa.be.agent.service.AgentProfileCrudService;
import com.navisa.be.application.service.ApplicationFormForAgentService;
import com.navisa.be.chat.dto.message.ChatMessageRequest;
import com.navisa.be.chat.dto.projection.ChatMessageNonReadCountProjection;
import com.navisa.be.chat.dto.projection.ChatRoomInfoProjection;
import com.navisa.be.chat.dto.projection.ChatRoomProposalStatusProjection;
import com.navisa.be.chat.dto.projection.LastMessageProjection;
import com.navisa.be.chat.dto.response.ChatRoomCardResponse;
import com.navisa.be.chat.exception.ChatRoomException;
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
import com.navisa.be.user.service.UserCrudService;
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
public class ChatRoomInteractService {

    private final UserCrudService userCrudService;
    private final ChatRoomSearchService chatRoomSearchService;
    private final ForeignerProfileCrudService foreignerProfileCrudService;
    private final AgentProfileCrudService agentProfileCrudService;
    private final ChatMessageSearchService chatMessageSearchService;
    private final ProposalService proposalService;
    private final ChatRoomRegistrationService chatRoomRegistrationService;
    private final StorageService storageService;
    private final ApplicationFormForAgentService applicationFormForAgentService;
    private final ChatIntegrationService chatIntegrationService;

    @Transactional(readOnly = true)
    public SliceResponse<ChatRoomCardResponse, Long> findAllChatRoomsByNoOffset(String email, String filter, SliceRequest<Long> slice) {
        ChatRoomFilterType filterType = ChatRoomFilterType.from(filter);

        User user = userCrudService.findByEmail(email);
        boolean isForeigner = user.getUserType().equals(UserType.FILLED_FOREIGNER);

        // 1. 자신의 프로필 ID 조회
        UUID profileId = isForeigner
                ? foreignerProfileCrudService.findByUserId(user.getId()).getId()
                : agentProfileCrudService.findByUserId(user.getId()).getId();

        // 2. 채팅방 목록 조회 (ExistsNext 확인을 위해 Repository에서 slice.size() + 1개를 가져와야 함)
        List<ChatRoomInfoProjection> chatRoomList = chatRoomSearchService.findChatRoomByProfileId(profileId, slice, isForeigner,
                filterType);

        if (chatRoomList.isEmpty())
            return new SliceResponse<>(List.of(), false, null);

        boolean existsNext = chatRoomList.size() > slice.size();
        List<ChatRoomInfoProjection> contentChatRooms = existsNext ? chatRoomList.subList(0, slice.size()) : chatRoomList;

        List<Long> contentChatRoomIds = contentChatRooms.stream().map(ChatRoomInfoProjection::chatRoomId).toList();

        Map<Long, String> lastMessageMap = chatMessageSearchService
                .findAllLastChatMessageByChatRoomIn(contentChatRoomIds)
                .stream()
                .collect(Collectors.toMap(
                        LastMessageProjection::chatRoomId,
                        LastMessageProjection::content,
                        (existing, replacement) -> existing));

        boolean allMatched = contentChatRooms.stream()
                .map(ChatRoomInfoProjection::chatRoomId)
                .allMatch(lastMessageMap::containsKey);

        if (!allMatched) {
            throw new ChatRoomException(ResponseStatus.INVALID_CHATMESSAGE);
        }

        Map<Long, Long> nonReadCountMap = chatMessageSearchService.findCountByChatRoomIdsIn(contentChatRoomIds, profileId)
                .stream()
                .collect(Collectors.toMap(
                        ChatMessageNonReadCountProjection::id,
                        ChatMessageNonReadCountProjection::count,
                        (existing, replacement) -> existing));

        Map<Long, ChatRoomProposalStatusProjection> proposalStatusMap = proposalService
                .findByChatRoomIdsIn(contentChatRoomIds)
                .stream()
                .collect(Collectors.toMap(
                        ChatRoomProposalStatusProjection::chatRoomId,
                        Function.identity(),
                        (existing, replacement) -> existing));

        // 3. 응답 DTO 변환 (상대방 프로필 정보 매핑)
        List<ChatRoomCardResponse> responses = contentChatRooms.stream()
                .map(projection -> new ChatRoomCardResponse(
                        projection.chatRoomId(),
                        getProfileImgUrl(projection, isForeigner),
                        projection.partnerName(),
                        projection.status(),
                        lastMessageMap.get(projection.chatRoomId()),
                        nonReadCountMap.getOrDefault(projection.chatRoomId(), 0L),
                        projection.lastChattedAt(),
                        hasReceivedProposal(projection.chatRoomId(), profileId, proposalStatusMap),
                        isProposalMatched(projection.chatRoomId(), proposalStatusMap)))
                .toList();

        Long lastElementId = contentChatRooms.isEmpty() ? null
                : contentChatRooms.get(contentChatRooms.size() - 1).chatRoomId();

        return new SliceResponse<>(responses, existsNext, lastElementId);
    }

    private String getProfileImgUrl(ChatRoomInfoProjection projection, boolean isForeigner) {
        if (isForeigner && projection.partnerProfileImageKey() != null) {
            return storageService.getImgUrl(
                    ImageSize.MEDIUM, projection.partnerProfileImageKey(), false);
        }
        return null;
    }

    private boolean hasReceivedProposal(Long chatRoomId, UUID profileId, Map<Long, ChatRoomProposalStatusProjection> proposalStatusMap) {
        if (!proposalStatusMap.containsKey(chatRoomId)) {
            return false;
        }
        ChatRoomProposalStatusProjection projection = proposalStatusMap.get(chatRoomId);
        // 내가 보낸 게 아닌 제안이 생성된 상태면 true
        return !projection.senderId().equals(profileId) && projection.status() == ProposalStatus.PROPOSED;
    }

    private boolean isProposalMatched(Long chatRoomId, Map<Long, ChatRoomProposalStatusProjection> proposalStatusMap) {
        return proposalStatusMap.containsKey(chatRoomId)
                && proposalStatusMap.get(chatRoomId).status() == ProposalStatus.MATCHED;
    }

    @Transactional
    public void updateBlockStatusToEntity(String email, Long chatRoomId, ChatMessageRequest request) {
        User user = userCrudService.findByEmail(email);

        ChatRoom chatRoom = chatRoomSearchService.findByIdWithProfiles(chatRoomId);

        validateChatRoomOwnership(user, chatRoom);

        chatRoomRegistrationService.updateStatus(chatRoom);
        proposalService.updateProposalOnBlock(chatRoom);
        applicationFormForAgentService.updateAgentProfileConnection(chatRoom);
        chatIntegrationService.saveAndPublishChatMessage(user.getId(), request, chatRoom);
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

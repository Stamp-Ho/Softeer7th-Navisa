package com.navisa.be.chat.service;

import com.navisa.be.agent.service.AgentProfileQueryService;
import com.navisa.be.chat.dto.projection.ChatMessageNonReadCountProjection;
import com.navisa.be.chat.dto.response.ChatRoomCardResponse;
import com.navisa.be.chat.exception.ChatRoomException;
import com.navisa.be.chat.model.entity.ChatMessage;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.common.dto.request.SliceRequest;
import com.navisa.be.common.dto.response.SliceResponse;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.foreigner.service.ForeignerQueryService;
import com.navisa.be.storage.model.enums.ImageSize;
import com.navisa.be.storage.service.AwsCloudfrontService;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceFacade {

    private final UserQueryService userQueryService;
    private final ChatRoomQueryService chatRoomQueryService;
    private final ForeignerQueryService foreignerQueryService;
    private final AgentProfileQueryService agentProfileQueryService;
    private final AwsCloudfrontService awsCloudfrontService;
    private final ChatMessageQueryService chatMessageQueryService;

    public SliceResponse<ChatRoomCardResponse, Long> findAllChatRoomsByNoOffset(String email, SliceRequest<Long> slice) {
        User user = userQueryService.findByEmail(email);
        boolean isForeigner = user.getUserType().equals(UserType.FILLED_FOREIGNER);

        // 1. 자신의 프로필 ID 조회
        UUID profileId = isForeigner
                ? foreignerQueryService.findByUserId(user.getId()).getId()
                : agentProfileQueryService.findByUserId(user.getId()).getId();

        // 2. 채팅방 목록 조회 (ExistsNext 확인을 위해 Repository에서 slice.size() + 1개를 가져와야 함)
        List<ChatRoom> chatRoomList = chatRoomQueryService.findChatRoomByProfileId(profileId, slice, isForeigner);

        if (chatRoomList.isEmpty())
            return new SliceResponse<>(List.of(), false, null);

        boolean existsNext = chatRoomList.size() > slice.size();
        List<ChatRoom> contentChatRooms = existsNext ? chatRoomList.subList(0, slice.size()) : chatRoomList;

        List<Long> contentChatRoomIds = contentChatRooms.stream().map(ChatRoom::getId).toList();

        Map<Long, String> lastMessageMap = chatMessageQueryService.findAllLastChatMessageByChatRoomIn(contentChatRoomIds)
                .stream()
                .collect(Collectors.toMap(
                        message -> message.getChatRoom().getId(),
                        ChatMessage::getContent,
                        (existing, replacement) -> existing
                ));

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
                        (existing, replacement) -> existing
                ));

        // 3. 응답 DTO 변환 (상대방 프로필 정보 매핑)
        List<ChatRoomCardResponse> responses = contentChatRooms.stream()
                .map(chatRoom -> ChatRoomCardResponse.toDto(
                        chatRoom,
                        isForeigner ? null
                                : awsCloudfrontService.getImageUrl(ImageSize.MEDIUM, chatRoom.getForeignerProfile().getProfileObjectKey()),
                        lastMessageMap.get(chatRoom.getId()),
                        nonReadCountMap.getOrDefault(chatRoom.getId(), 0L),
                        isForeigner)
                ).toList();

        Long lastElementId = contentChatRooms.isEmpty() ? null : contentChatRooms.get(contentChatRooms.size() - 1).getId();

        return new SliceResponse<>(responses, existsNext, lastElementId);
    }
}

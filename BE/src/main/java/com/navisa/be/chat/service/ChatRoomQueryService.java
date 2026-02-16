package com.navisa.be.chat.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.service.AgentBadgeService;
import com.navisa.be.chat.dto.response.GetChatRoomParticipantsInfoResponse;
import com.navisa.be.chat.exception.ChatRoomException;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ChatRoomFilterType;
import com.navisa.be.chat.repository.ChatRoomRepository;
import com.navisa.be.global.web.request.SliceRequest;
import com.navisa.be.global.common.model.entity.Nationality;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.foreigner.model.entity.ForeignerExpectedCompany;
import com.navisa.be.foreigner.model.entity.ForeignerNationality;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.service.ForeignerQueryService;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.service.UserQueryService;
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
    private final UserQueryService userQueryService;
    private final AgentBadgeService agentBadgeService;
    private final ForeignerQueryService foreignerQueryService;

    // 외국인이 자신의 채팅방을 조회
    public List<ChatRoom> findChatRoomByProfileId(
            UUID foreignerId, SliceRequest<Long> slice, boolean isForeignerId, ChatRoomFilterType filter) {

        return chatRoomRepository.findByNoOffset(foreignerId, slice, isForeignerId, filter);
    }

    public List<Long> findChatRoomsByForeignerId(UUID foreignerId) {
        return chatRoomRepository.findAllIdsByForeignerProfileId(foreignerId);
    }

    public List<Long> findChatRoomsByAgentId(UUID agentId) {
        return chatRoomRepository.findAllIdsByAgentProfileId(agentId);
    }

    public ChatRoom findById(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatRoomException(ResponseStatus.INVALID_CHATROOM));
    }

    public ChatRoom findByIdWithProfiles(Long roomId) {
        return chatRoomRepository.findByIdWithProfiles(roomId)
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

    @Transactional
    public GetChatRoomParticipantsInfoResponse findParticipantsInfoById(Long roomId, String loginUserEmail) {
        User loginUser = userQueryService.findByEmail(loginUserEmail);

        ChatRoom chatRoom = chatRoomRepository.findByIdWithParticipantsInfo(roomId)
                .orElseThrow(() -> new ChatRoomException(ResponseStatus.NOT_FOUND_CHATROOM));

        // 요청자가 대화 참여자가 아니면 예외
        if (!(chatRoom.getAgentProfile().getUserId().equals(loginUser.getId())
                || chatRoom.getForeignerProfile().getUserId().equals(loginUser.getId()))) {
            throw new ChatRoomException(ResponseStatus.NOT_ALLOWED_TO_ACCESS_CHATROOM);
        }

        // 행정사는 외국인의 정보를 조회
        if (loginUser.getUserType() == UserType.VALID_AGENT) {
            ForeignerProfile foreignerProfile = chatRoom.getForeignerProfile();
            ForeignerExpectedCompany expectedCompany = foreignerQueryService.findExpectedCompanyByForeignerProfileId(foreignerProfile.getId());
            List<Long> nationalityIds = foreignerProfile.getForeignerNationalities().stream()
                    .map(ForeignerNationality::getNationality)
                    .map(Nationality::getId)
                    .toList();

            return GetChatRoomParticipantsInfoResponse.entityToDto(foreignerProfile, expectedCompany, nationalityIds);
        }

        // 외국인은 행정사의 정보를 조회
        AgentProfile agentProfile = chatRoom.getAgentProfile();
        List<Long> top2BadgeIds = agentBadgeService.getTop2BadgeIds(agentProfile.getId());

        return GetChatRoomParticipantsInfoResponse.entityToDto(
                agentProfile,
                top2BadgeIds
        );
    }

    public Optional<ChatRoom> findByAgentIdAndForeignerId(UUID agentId, UUID foreignerId) {
        return chatRoomRepository.findByAgentIdAndForeignerId(agentId, foreignerId);
    }
}

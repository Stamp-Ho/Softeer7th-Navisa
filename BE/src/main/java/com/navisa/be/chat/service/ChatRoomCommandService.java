package com.navisa.be.chat.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.service.AgentProfileQueryService;
import com.navisa.be.chat.dto.request.CreateChatRoomRequest;
import com.navisa.be.chat.dto.response.CreateChatRoomResponse;
import com.navisa.be.chat.exception.ChatRoomException;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import com.navisa.be.chat.repository.ChatRoomRepository;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.service.ForeignerQueryService;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomCommandService {

    private final ChatRoomRepository chatRoomRepository;
    private final AgentProfileQueryService agentProfileQueryService;
    private final ForeignerQueryService foreignerQueryService;
    private final UserQueryService userQueryService;
    private final ChatMessageService chatMessageService;
    private final ChatRoomQueryService chatRoomQueryService;

    public void updateStatus(ChatRoom chatRoom) {
        chatRoom.updateStatus(ChatRoomStatus.BLOCKED);
    }

    @Transactional
    public CreateChatRoomResponse create(CreateChatRoomRequest request, String loginUserEmail) {
        User loginUser = userQueryService.findByEmail(loginUserEmail);

        if(loginUser.getUserType() != UserType.VALID_AGENT && loginUser.getUserType() != UserType.FILLED_FOREIGNER){
            throw new ChatRoomException(ResponseStatus.FORBIDDEN);
        }

        // 행정사와 외국인을 조회
        AgentProfile agentProfile = getAgentProfile(loginUser, request.opponentProfileId());
        ForeignerProfile foreignerProfile = getForeignerProfile(loginUser, request.opponentProfileId());

        // 방이 있으면 예외
        if (chatRoomQueryService.existsByAgentIdAndForeignerId(agentProfile.getId(), foreignerProfile.getId())) {
            throw new ChatRoomException(ResponseStatus.CHATROOM_ALREADY_EXIST);
        }

        // 방을 생성
        ChatRoom chatRoom;
        try{
            chatRoom = chatRoomRepository.save(new ChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT, request.sendAt()));
            chatRoomRepository.flush();
        }
        catch (DataIntegrityViolationException e) {
            log.warn("채팅방 동시 생성 시도 발생: agent={}, foreigner={}", agentProfile.getId(), foreignerProfile.getId());
            throw new ChatRoomException(ResponseStatus.CHATROOM_ALREADY_EXIST);
        }

        // 메시지를 저장
        UUID senderId = (loginUser.getUserType() == UserType.VALID_AGENT) ? agentProfile.getId() : foreignerProfile.getId();
        chatMessageService.create(chatRoom, senderId, request.content(), request.sendAt());

        return new CreateChatRoomResponse(chatRoom.getId());
    }

    private ForeignerProfile getForeignerProfile(User loginUser, UUID opponentProfileId) {
        if (loginUser.getUserType() == UserType.VALID_AGENT) {
            return foreignerQueryService.findById(opponentProfileId);
        }
        return foreignerQueryService.findByUserId(loginUser.getId());
    }

    private AgentProfile getAgentProfile(User loginUser, UUID opponentProfileId) {
        if (loginUser.getUserType() == UserType.VALID_AGENT) {
            return agentProfileQueryService.findByUserId(loginUser.getId());
        }
        return agentProfileQueryService.findById(opponentProfileId);
    }
}

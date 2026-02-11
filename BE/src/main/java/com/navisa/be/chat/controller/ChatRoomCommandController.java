package com.navisa.be.chat.controller;

import com.navisa.be.chat.service.ChatRoomServiceFacade;
import com.navisa.be.common.annotation.HasUserType;
import com.navisa.be.common.annotation.LoginUser;
import com.navisa.be.common.dto.response.BaseResponse;
import com.navisa.be.user.model.enums.UserType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ChatRoom의 CUD API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatroom")
public class ChatRoomCommandController {

    private final ChatRoomServiceFacade chatRoomServiceFacade;

    @Operation(
            summary = "채팅방 내에서 상대방 차단 API",
            description = "채팅방에서 상대방을 차단하여 채팅방 상태와 수임 제안 상태를 차단상태로 바꾸고, 비자신청서와의 행정사 연결을 해제합니다."
    )
    @HasUserType({UserType.FILLED_FOREIGNER, UserType.VALID_AGENT})
    @PostMapping("/{roomId}/block")
    public BaseResponse<Void> blockChatRoom(
            @Parameter(hidden = true) @LoginUser String email,
            @PathVariable(name = "roomId") Long chatRoomId) {

        chatRoomServiceFacade.updateBlockStatusToEntity(email, chatRoomId);
        return new BaseResponse<>(null);
    }
}

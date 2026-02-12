package com.navisa.be.chat.controller;

import com.navisa.be.chat.service.ChatRoomServiceFacade;
import com.navisa.be.chat.dto.request.CreateChatRoomRequest;
import com.navisa.be.chat.dto.response.CreateChatRoomResponse;
import com.navisa.be.chat.service.ChatRoomCommandService;
import com.navisa.be.common.annotation.HasUserType;
import com.navisa.be.common.annotation.LoginUser;
import com.navisa.be.common.dto.response.BaseResponse;
import com.navisa.be.user.model.enums.UserType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "ChatRoom Command",
        description = "채팅방 생성/수정/삭제 관련 API"
)
@RequiredArgsConstructor
@RequestMapping("/api/chatroom")
@RestController
public class ChatRoomCommandController {

    private final ChatRoomServiceFacade chatRoomServiceFacade;
    private final ChatRoomCommandService chatRoomCommandService;

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

    @Operation(
            summary = "새로운 채팅방 생성",
            description = "행정사와 외국인이 새로운 채팅방을 생성할 때 호출하는 API입니다. 다음 노션 링크를 참고해주세요. https://www.notion.so/bside/30222020273580b893baf29847839b20?source=copy_link"
    )
    @HasUserType({UserType.VALID_AGENT, UserType.FILLED_FOREIGNER})
    @PostMapping
    public BaseResponse<CreateChatRoomResponse> createChatRoom(
            @Valid @RequestBody CreateChatRoomRequest request,
            @Parameter(hidden = true) @LoginUser String loginUserEmail
    ) {
        return new BaseResponse<>(chatRoomCommandService.create(request, loginUserEmail));
    }
}

package com.navisa.be.chat.controller;

import com.navisa.be.chat.dto.response.ChatMessageCountResponse;
import com.navisa.be.chat.dto.response.ChatRoomCardResponse;
import com.navisa.be.chat.service.ChatMessageServiceFacade;
import com.navisa.be.chat.service.ChatRoomServiceFacade;
import com.navisa.be.common.annotation.HasUserType;
import com.navisa.be.common.annotation.LoginUser;
import com.navisa.be.common.annotation.SliceInfo;
import com.navisa.be.common.dto.request.SliceRequest;
import com.navisa.be.common.dto.response.BaseResponse;
import com.navisa.be.common.dto.response.SliceResponse;
import com.navisa.be.user.model.enums.UserType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/chatrooms")
@RestController
@RequiredArgsConstructor
@Tag(name = "ChatRoom", description = "채팅방 조회 API")
public class ChatRoomQueryController {

    private final ChatRoomServiceFacade chatRoomServiceFacade;
    private final ChatMessageServiceFacade chatMessageServiceFacade;

    @GetMapping
    @HasUserType({UserType.FILLED_FOREIGNER, UserType.VALID_AGENT})
    @Operation(summary = "채팅방 목록 조회", description = "로그인한 사용자의 채팅방 목록을 페이징하여 조회합니다.")
    public BaseResponse<SliceResponse<ChatRoomCardResponse, Long>> getChatRooms(
            @SliceInfo(size = 10, max = 10) SliceRequest<Long> slice,
            @Parameter(description = "필터", example = "unread | matched") @RequestParam(required = false) String filter,

            @Parameter(hidden = true) @LoginUser String email) {

        return new BaseResponse<>(chatRoomServiceFacade.findAllChatRoomsByNoOffset(email, filter, slice));
    }

    @GetMapping("/nonread/count")
    @HasUserType({UserType.FILLED_FOREIGNER, UserType.VALID_AGENT})
    @Operation(summary = "안 읽은 메시지 수 조회", description = "로그인한 사용자의 전체 안 읽은 메시지 수를 조회합니다.")
    public BaseResponse<ChatMessageCountResponse> getChatMessageNonReadCount(
            @Parameter(hidden = true) @LoginUser String email) {

        ChatMessageCountResponse content = chatMessageServiceFacade.findNonReadCountByUserEmail(email);
        return new BaseResponse<>(content);
    }

    @GetMapping("/matched/count")
    @HasUserType({UserType.VALID_AGENT})
    @Operation(summary = "행정사의 매칭된 채팅방 중 안읽은 메시지 수 조회", description = "행정사의 매칭된 채팅방 중 안 읽은 전체 메시지 개수를 조회합니다.")
    public BaseResponse<ChatMessageCountResponse> getChatMessageMatchedNonReadCount(
            @Parameter(hidden = true) @LoginUser String email) {

        ChatMessageCountResponse content = chatMessageServiceFacade.findMatchedNonReadCountByUserEmail(email);
        return new BaseResponse<>(content);
    }
}

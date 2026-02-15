package com.navisa.be.chat.controller;

import com.navisa.be.chat.dto.response.ChatMessageCountResponse;
import com.navisa.be.chat.dto.response.ChatRoomCardResponse;
import com.navisa.be.chat.dto.response.GetChatRoomParticipantsInfoResponse;
import com.navisa.be.chat.service.ChatMessageServiceFacade;
import com.navisa.be.chat.service.ChatRoomQueryService;
import com.navisa.be.chat.service.ChatRoomServiceFacade;
import com.navisa.be.global.web.annotation.HasUserType;
import com.navisa.be.global.web.annotation.LoginUser;
import com.navisa.be.global.web.annotation.SliceInfo;
import com.navisa.be.global.web.request.SliceRequest;
import com.navisa.be.global.web.response.BaseResponse;
import com.navisa.be.global.web.response.SliceResponse;
import com.navisa.be.user.model.enums.UserType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/chatrooms")
@RestController
@RequiredArgsConstructor
@Tag(name = "ChatRoom", description = "채팅방 조회 API")
public class ChatRoomQueryController {

    private final ChatRoomServiceFacade chatRoomServiceFacade;
    private final ChatMessageServiceFacade chatMessageServiceFacade;
    private final ChatRoomQueryService chatRoomQueryService;

    @GetMapping
    @HasUserType({UserType.FILLED_FOREIGNER, UserType.VALID_AGENT})
    @Operation(
            summary = "채팅방 목록 조회",
            description = "로그인한 사용자의 채팅방 목록을 페이징하여 조회합니다.",
            parameters = {
                    @Parameter(name = "lastElementId", description = "마지막으로 조회한 채팅방 ID (첫 페이지는 생략)", schema = @io.swagger.v3.oas.annotations.media.Schema(type = "integer", format = "int64")),
                    @Parameter(name = "size", description = "페이지 크기 (기본값: 10, 최대: 10)", schema = @io.swagger.v3.oas.annotations.media.Schema(type = "integer"))

            }
    )
    public BaseResponse<SliceResponse<ChatRoomCardResponse, Long>> getChatRooms(
            @ParameterObject @SliceInfo(size = 10, max = 10) SliceRequest<Long> slice,
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

    @HasUserType({UserType.VALID_AGENT, UserType.FILLED_FOREIGNER})
    @PostMapping("/{roomId}/participants-info")
    @Operation(
            summary = "특정 채팅방 참여자 정보 조회",
            description = "특정 채팅방에 참여하는 유저들의 정보를 조회할 수 있는 API입니다. 관련 노션 링크 : https://www.notion.so/bside/305220202735808aa3f7eb052902d4fc?source=copy_link "
    )
    public BaseResponse<GetChatRoomParticipantsInfoResponse> getChatRoomParticipantsInfo(
            @PathVariable Long roomId,
            @Parameter(hidden = true) @LoginUser String loginUserEmail) {
        return new BaseResponse<>(chatRoomQueryService.findParticipantsInfoById(roomId, loginUserEmail));
    }
}

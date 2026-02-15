package com.navisa.be.chat.controller;

import com.navisa.be.chat.dto.response.ChatMessageSimpleResponse;
import com.navisa.be.chat.service.ChatMessageServiceFacade;
import com.navisa.be.global.web.annotation.HasUserType;
import com.navisa.be.global.web.annotation.LoginUser;
import com.navisa.be.global.web.annotation.SliceInfo;
import com.navisa.be.global.web.request.SliceRequest;
import com.navisa.be.global.web.response.BaseResponse;
import com.navisa.be.global.web.response.SliceResponse;
import com.navisa.be.user.model.enums.UserType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatroom")
public class ChatMessageQueryController {

    private final ChatMessageServiceFacade chatMessageServiceFacade;

    @HasUserType({ UserType.FILLED_FOREIGNER, UserType.VALID_AGENT })
    @GetMapping("/{chatRoomId}/messages")
    @Operation(summary = "특정 채팅방의 무한 스크롤 방식 채팅 메세지 조회(기본 size는 20)", description = "관련 노션 링크(https://www.notion.so/bside/47f1a07246c84cdeb0a394b478d9a23b?v=2ed22020273580059255000cb37a5c85&source=copy_link)")
    public BaseResponse<SliceResponse<ChatMessageSimpleResponse, Long>> findChatHistoryByChatRoomIdAndEndlessScroll(
            @Parameter(hidden = true) @LoginUser String email,
            @SliceInfo(size = 20) SliceRequest<Long> slice,
            @PathVariable Long chatRoomId) {

        return new BaseResponse<>(chatMessageServiceFacade.findChatMessagesByChatRoomIdAndNoOffset(email, chatRoomId, slice));
    }
}

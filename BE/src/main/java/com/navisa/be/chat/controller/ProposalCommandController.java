package com.navisa.be.chat.controller;

import com.navisa.be.chat.dto.message.ChatMessageRequest;
import com.navisa.be.chat.service.ProposalService;
import com.navisa.be.global.web.annotation.HasUserType;
import com.navisa.be.global.web.annotation.LoginUser;
import com.navisa.be.global.web.response.BaseResponse;
import com.navisa.be.user.model.enums.UserType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "수임 제안 관련 API",
        description = "수임 제안과 관련된 API 목록입니다."
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatroom")
public class ProposalCommandController {

    private final ProposalService proposalService;

    @HasUserType({UserType.FILLED_FOREIGNER, UserType.VALID_AGENT})
    @PostMapping("/{roomId}/proposal")
    @Operation(summary = "수임 제안 생성 API", description = "수임 제안을 생성하는 API입니다.")
    public BaseResponse<Void> createProposal(
            @Parameter(hidden = true) @LoginUser String email, @PathVariable Long roomId,
            @Valid @RequestBody ChatMessageRequest request) {
        proposalService.createProposal(email, roomId, request);
        return new BaseResponse<>(null);
    }

    @HasUserType({UserType.FILLED_FOREIGNER, UserType.VALID_AGENT})
    @PostMapping("/{roomId}/proposal/accepted")
    @Operation(summary = "수임 제안 수락 API", description = "수임 제안을 수락하는 API입니다.")
    public BaseResponse<Void> updateProposalStatusMatched(
            @Parameter(hidden = true) @LoginUser String email, @PathVariable Long roomId,
            @Valid @RequestBody ChatMessageRequest request) {

        proposalService.updateProposalStatusMatched(email, roomId, request);
        return new BaseResponse<>(null);
    }

    @HasUserType({UserType.FILLED_FOREIGNER, UserType.VALID_AGENT})
    @PostMapping("/{roomId}/proposal/rejected")
    @Operation(summary = "수임 제안 거절 API", description = "수임 제안을 거절하는 API입니다.")
    public BaseResponse<Void> updateProposalStatusRejected(
            @Parameter(hidden = true) @LoginUser String email, @PathVariable Long roomId,
            @Valid @RequestBody ChatMessageRequest request) {

        proposalService.updateProposalStatusRejected(email, roomId, request);
        return new BaseResponse<>(null);
    }

    @HasUserType({UserType.FILLED_FOREIGNER, UserType.VALID_AGENT})
    @PostMapping("/{roomId}/proposal/canceled")
    @Operation(summary = "수임 제안 취소 API", description = "수임 제안을 취소하는 API입니다.")
    public BaseResponse<Void> updateProposalStatusCanceled(
            @Parameter(hidden = true) @LoginUser String email, @PathVariable Long roomId,
            @Valid @RequestBody ChatMessageRequest request) {

        proposalService.updateProposalStatusCanceled(email, roomId, request);
        return new BaseResponse<>(null);
    }
}

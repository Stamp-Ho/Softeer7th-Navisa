package com.navisa.be.agent.controller;

import com.navisa.be.agent.dto.request.*;
import com.navisa.be.agent.dto.response.AgentCardResponse;
import com.navisa.be.agent.dto.response.JobCodeListResponse;
import com.navisa.be.agent.service.*;
import com.navisa.be.global.common.service.JobCodeService;
import com.navisa.be.global.web.annotation.HasUserType;
import com.navisa.be.agent.dto.response.AgentDetailResponse;
import com.navisa.be.global.web.annotation.LoginUser;
import com.navisa.be.global.web.annotation.SliceInfo;
import com.navisa.be.global.web.request.SliceRequest;
import com.navisa.be.global.web.response.BaseResponse;
import com.navisa.be.global.web.response.SliceResponse;
import com.navisa.be.user.model.enums.UserType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Agent Profile", description = "행정사 프로필 등록 및 탐색 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/agent")
@RestController
public class AgentProfileController {

    private final AgentProfileRegistrationService agentProfileRegistrationService;
    private final AgentProfileSearchService agentProfileSearchService;
    private final JobCodeService jobCodeService;

    @Operation(
            summary = "행정사 프로필 등록 API",
            description = "행정사가 프로필을 등록하기 위해서 사용하는 API입니다."
    )
    @PostMapping("/profile")
    public BaseResponse<Void> registerAgentProfile(@Valid @RequestBody AgentProfileRegistrationRequest request,
                                                   @Parameter(hidden = true) @LoginUser String loginUserEmail) {
        agentProfileRegistrationService.registerAgentProfile(request, loginUserEmail);
        return new BaseResponse<>(null);
    }

    @Operation(
            summary = "행정사 프로필 필터 검색 API",
            description = "직무, 지역, 언어 필터를 기반으로 행정사 목록을 조회합니다. No-Offset 방식의 Slice 페이징을 지원합니다.",
            parameters = {
                    @Parameter(name = "lastElementId", description = "마지막으로 조회한 행정사 ID (첫 페이지는 생략)", schema = @Schema(type = "string", format = "uuid")),
                    @Parameter(name = "size", description = "페이지 크기 (기본값: 16, 최대: 16)", schema = @Schema(type = "integer"))
            }
    )
    @HasUserType({UserType.FILLED_FOREIGNER, UserType.UNFILLED_FOREIGNER})
    @GetMapping("/cards")
    public BaseResponse<SliceResponse<AgentCardResponse, UUID>> findAgentProfileCardsBasedOnFilter(
            @Parameter(description = "조회 필터 (콤마로 구분, 대괄호 쓰지 않고 요청하기)", example = """
                    {
                      "jobGroupNameList": "",
                      "regionList": "서울,부산",
                      "languageIdList": "1,2,3"
                    }""") @ModelAttribute AgentCardRequest request,
            @Parameter(description = "페이징 정보 (lastElementId, size)") @ParameterObject @SliceInfo(max = 16) SliceRequest<UUID> slice,
            @Parameter(hidden = true) @LoginUser String email) {

        return new BaseResponse<>(agentProfileSearchService.findAgentProfileCardsBasedOnFilter(request, slice, email));
    }

    @Operation(
            summary = "행정사 프로필 등록 중 직무코드 리스트 조회 API",
            description = "행정사가 프로필 등록 과정에서 직무코드 목록을 조회할 때 사용하는 API입니다."
    )
    @GetMapping("/register-form/jobcodes")
    public BaseResponse<JobCodeListResponse> getJobCodeList(){
        JobCodeListResponse response = jobCodeService.getJobCodeList();
        return new BaseResponse<>(response);
    }

    @Operation(
            summary = "외국인과 행정사의 행정사 상세 조회 API",
            description = "외국인과 행정사가 특정 행정사를 상세 조회하는 API입니다"
    )
    @HasUserType({UserType.VALID_AGENT, UserType.FILLED_FOREIGNER})
    @GetMapping("/{agentId}")
    public BaseResponse<AgentDetailResponse> getAgentDetail(@PathVariable("agentId") UUID agentId,
                                                            @Parameter(hidden = true) @LoginUser String loginUserEmail){
        AgentDetailResponse response = agentProfileSearchService.getAgentDetail(loginUserEmail, agentId);
        return new BaseResponse<>(response);
    }
}

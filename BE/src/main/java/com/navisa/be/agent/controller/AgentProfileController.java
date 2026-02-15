package com.navisa.be.agent.controller;

import com.navisa.be.agent.dto.request.*;
import com.navisa.be.agent.dto.response.AgentCardResponse;
import com.navisa.be.agent.dto.response.GetJobCodeListResponse;
import com.navisa.be.agent.service.*;
import com.navisa.be.global.web.annotation.HasUserType;
import com.navisa.be.agent.dto.response.GetAgentDetailResponse;
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

@Tag(name = "Agent Profile", description = "행정사 프로필 API")
@RequiredArgsConstructor
@RequestMapping("/api/agent")
@RestController
public class AgentProfileController {

    private final AgentProfileCommandService agentProfileCommandService;
    private final JobCodeService jobCodeService;
    private final AgentProfileServiceFacade agentProfileServiceFacade;
    private final AgentProfileQueryService agentProfileQueryService;
    private final AgentReviewService agentReviewService;

    @Operation(
            summary = "행정사 프로필 등록 API",
            description = "행정사가 프로필을 등록하기 위해서 사용하는 API입니다. 추가적인 정보는 https://www.notion.so/bside/15443d2553684a4fb58c43a38c54a6af?source=copy_link를 참고해주세요"
    )
    @PostMapping("/profile")
    public BaseResponse<Void> registerAgentProfile(@Valid @RequestBody RegisterAgentProfileRequest request,
                                                   @Parameter(hidden = true) @LoginUser String loginUserEmail) {
        RegisterAgentProfileCommand command = new RegisterAgentProfileCommand(request, loginUserEmail);
        agentProfileCommandService.registerAgentProfile(command);
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

        return new BaseResponse<>(agentProfileServiceFacade.findAgentProfileCardsBasedOnFilter(request, slice, email));
    }

    @Operation(
            summary = "행정사 프로필 등록 중 직무코드 리스트 조회 API",
            description = "행정사가 프로필 등록 과정에서 직무코드 목록을 조회할 때 사용하는 API입니다. 추가적인 정보는 https://www.notion.so/bside/2fa22020273580268a2ec408b30182af?source=copy_link를 참고해주세요"
    )
    @GetMapping("/register-form/jobcodes")
    public BaseResponse<GetJobCodeListResponse> getJobCodeList() {
        GetJobCodeListResponse response = jobCodeService.getJobCodeList();
        return new BaseResponse<>(response);
    }

    @Operation(
            summary = "외국인과 행정사의 행정사 상세 조회 API",
            description = "외국인과 행정사가 특정 행정사를 상세 조회하는 API입니다. 추가적인 정보는 https://www.notion.so/bside/2fb22020273580059241f9d855571532?source=copy_link를 참고해주세요"
    )
    @HasUserType({UserType.VALID_AGENT, UserType.FILLED_FOREIGNER})
    @GetMapping("/{agentId}")
    public BaseResponse<GetAgentDetailResponse> getAgentDetail(@PathVariable("agentId") UUID agentId,
                                                               @Parameter(hidden = true) @LoginUser String loginUserEmail) {
        GetAgentDetailResponse response = agentProfileQueryService.getAgentDetail(loginUserEmail, agentId);
        return new BaseResponse<>(response);
    }

    @Operation(
            summary = "외국인의 행정사 리뷰 작성 API",
            description = "외국인이 특정 행정사에게 리뷰를 작성할 때 사용하는 API입니다. 추가적인 정보는 https://www.notion.so/bside/2ef22020273581d18476d1b8c10eb041?source=copy_link를 참고해주세요"
    )
    @HasUserType(UserType.FILLED_FOREIGNER)
    @PostMapping("/reviews")
    public BaseResponse<Void> createAgentReview(@Valid @RequestBody CreateAgentReviewRequest request,
                                                @Parameter(hidden = true) @LoginUser String loginUserEmail) {
        agentReviewService.createAgentReview(loginUserEmail, request);
        return new BaseResponse<>(null);
    }

    @Operation(
            summary = "외국인의 행정사 피드백 등록 API",
            description = "리뷰 작성 후, 해당 계약 건에 대해 구체적인 피드백 내용을 등록합니다."
    )
    @HasUserType(UserType.FILLED_FOREIGNER)
    @PostMapping("/feedback")
    public BaseResponse<Void> createAgentFeedback(@Valid @RequestBody CreateAgentFeedbackRequest request,
                                                  @Parameter(hidden = true) @LoginUser String loginUserEmail) {
        agentReviewService.createAgentFeedback(loginUserEmail, request.content());
        return new BaseResponse<>(null);
    }
}

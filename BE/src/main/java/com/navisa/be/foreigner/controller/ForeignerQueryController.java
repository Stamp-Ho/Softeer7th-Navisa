package com.navisa.be.foreigner.controller;

import com.navisa.be.common.annotation.HasUserType;
import com.navisa.be.common.annotation.LoginUser;
import com.navisa.be.common.annotation.SliceInfo;
import com.navisa.be.common.dto.request.SliceRequest;
import com.navisa.be.common.dto.response.BaseResponse;
import com.navisa.be.common.dto.response.SliceResponse;
import com.navisa.be.foreigner.dto.request.ForeignerCardRequest;
import com.navisa.be.foreigner.dto.response.ForeignerCardExtensionResponse;
import com.navisa.be.foreigner.dto.request.FindForeignerDetailCommand;
import com.navisa.be.foreigner.dto.response.FindForeignerDetailResponse;
import com.navisa.be.foreigner.dto.response.ForeignerCardResponse;
import com.navisa.be.foreigner.dto.response.ForeignerQueryResponse;
import com.navisa.be.foreigner.dto.response.ForeignerStatusResponse;
import com.navisa.be.foreigner.service.ForeignerQueryService;
import com.navisa.be.foreigner.service.ForeignerServiceFacade;
import com.navisa.be.user.model.enums.UserType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/foreigner")
@AllArgsConstructor
@Tag(name = "Foreigner Query", description = "외국인 프로필 조회 API")
public class ForeignerQueryController {

    private final ForeignerServiceFacade foreignerServiceFacade;
    private final ForeignerQueryService foreignerQueryService;

    @Operation(summary = "내 프로필 조회", description = "현재 로그인한 외국인 회원의 전체 프로필 정보를 조회합니다.")
    @GetMapping("/profile")
    public BaseResponse<ForeignerQueryResponse> findForeignerProfile(
            @Parameter(hidden = true) @LoginUser String email) {
        ForeignerQueryResponse response = foreignerServiceFacade.findForeignerTotalInfo(email);
        return new BaseResponse<>(response);
    }

    @Operation(summary = "외국인 상세 요건 입력 여부 확인", description = "현재 로그인한 외국인 회원의 필수 상세 요건 입력 상태를 조회합니다.")
    @GetMapping("/requirements")
    public BaseResponse<ForeignerStatusResponse> checkForeignerFilledStatus(
            @Parameter(hidden = true) @LoginUser String email) {
        ForeignerStatusResponse response = foreignerQueryService.checkForeignerFilledStatus(email);
        return new BaseResponse<>(response);
    }

    @Operation(
            summary = "행정사의 특화 직무코드와 매칭되는 최신순 외국인 프로필 10개 조회",
            description = "현재 로그인한 행정사 회원이 가진 특화 직무코드와 매칭되는 외국인 프로필 정보를 최신순으로 조회합니다. 노션 링크 : https://www.notion.so/bside/1f673f3551fd4d6888a7d579711e67fe?source=copy_link"
    )
    @HasUserType({UserType.VALID_AGENT})
    @GetMapping("/home")
    public BaseResponse<List<ForeignerCardResponse>> findMatchedForeignerCard(
            @Parameter(hidden = true) @LoginUser String email) {
        List<ForeignerCardResponse> cards = foreignerQueryService.findForeignerCardMatchOnSpecializedJob(email);
        return new BaseResponse<>(cards);
    }

    @Operation(
            summary = "외국인 프로필 필터 검색 API",
            description = "직무, 지역, 언어 필터를 기반으로 외국인 목록을 조회합니다. No-Offset 방식의 Slice 페이징을 지원합니다."
    )
    @HasUserType({ UserType.VALID_AGENT })
    @GetMapping("/cards")
    public BaseResponse<SliceResponse<ForeignerCardExtensionResponse, UUID>> findForeignerProfileCardsBasedOnFilter(
            @ModelAttribute ForeignerCardRequest request,
            @Parameter(description = "페이징 정보 (lastElementId: 마지막으로 본 외국인 ID, size: 페이지 크기)") @SliceInfo(max = 16) SliceRequest<UUID> slice) {

        return new BaseResponse<>(foreignerServiceFacade.findForeignerProfileCardsBasedOnFilter(request, slice));
    }

    @Operation(
            summary = "행정사의 외국인 상세 조회",
            description = "승인된 행정사 회원이 외국인을 상세 조회할 때 사용되는 API입니다. 노션 링크 : https://www.notion.so/bside/3a3e1b2a8c7947d0be4c828e2df64bb9?source=copy_link"
    )
    @GetMapping("/{foreignerId}")
    @HasUserType({UserType.VALID_AGENT})
    public BaseResponse<FindForeignerDetailResponse> findForeignerDetail(@PathVariable UUID foreignerId,
                                                                         @Parameter(hidden = true) @LoginUser String loginUserEmail) {
        FindForeignerDetailCommand request = new FindForeignerDetailCommand(loginUserEmail, foreignerId);
        FindForeignerDetailResponse response = foreignerQueryService.findForeignerDetail(request);
        return new BaseResponse<>(response);
    }
}

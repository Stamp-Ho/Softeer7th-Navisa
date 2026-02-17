package com.navisa.be.foreigner.controller;

import com.navisa.be.foreigner.dto.request.ForeignerCardRequest;
import com.navisa.be.foreigner.dto.response.ForeignerCardExtensionResponse;
import com.navisa.be.foreigner.dto.response.ForeignerCardResponse;
import com.navisa.be.foreigner.service.ForeignerProfileSearchService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/foreigner")
@RequiredArgsConstructor
@Tag(name = "Foreigner Search", description = "외국인 검색 및 매칭 API")
public class ForeignerSearchController {

    private final ForeignerProfileSearchService foreignerProfileSearchService;

    @Operation(
            summary = "행정사의 특화 직무코드와 매칭되는 최신순 외국인 프로필 10개 조회",
            description = "현재 로그인한 행정사 회원이 가진 특화 직무코드와 매칭되는 외국인 프로필 정보를 최신순으로 조회합니다."
    )
    @HasUserType({UserType.VALID_AGENT})
    @GetMapping("/home")
    public BaseResponse<List<ForeignerCardResponse>> findMatchedForeignerCard(
            @Parameter(hidden = true) @LoginUser String email) {
        List<ForeignerCardResponse> cards = foreignerProfileSearchService.findForeignerCardMatchOnSpecializedJob(email);
        return new BaseResponse<>(cards);
    }

    @Operation(
            summary = "외국인 프로필 필터 검색 API",
            description = "직무, 지역, 언어 필터를 기반으로 외국인 목록을 조회합니다. No-Offset 방식의 Slice 페이징을 지원합니다."
    )
    @HasUserType({UserType.VALID_AGENT})
    @GetMapping("/cards")
    public BaseResponse<SliceResponse<ForeignerCardExtensionResponse, UUID>> findForeignerProfileCardsBasedOnFilter(
            @ModelAttribute ForeignerCardRequest request,
            @Parameter(description = "페이징 정보 (lastElementId: 마지막으로 본 외국인 ID, size: 페이지 크기)") @SliceInfo(max = 16) SliceRequest<UUID> slice) {

        return new BaseResponse<>(foreignerProfileSearchService.findForeignerProfileCardsBasedOnFilter(request, slice));

    }
}

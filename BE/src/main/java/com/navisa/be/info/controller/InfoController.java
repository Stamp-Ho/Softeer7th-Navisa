package com.navisa.be.info.controller;

import com.navisa.be.common.dto.response.BaseResponse;
import com.navisa.be.info.dto.response.GetLanguageListResponse;
import com.navisa.be.info.service.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Information",
        description = "서비스에 필요한 정보를 제공하는 API들"
)
@RequiredArgsConstructor
@RequestMapping("/api/info")
@RestController
public class InfoController {

    private final LanguageService languageService;

    @Operation(
            summary = "사용가능 언어 목록 조회 API",
            description = "외국인과 행정사의 프로필 등록에서 필요한 사용가능 언어 목록 조회 API입니다"
    )
    @GetMapping("/languages")
    public BaseResponse<GetLanguageListResponse> getLanguageList(){
        GetLanguageListResponse response = languageService.getLanguageList();
        return new BaseResponse<>(response);
    }
}

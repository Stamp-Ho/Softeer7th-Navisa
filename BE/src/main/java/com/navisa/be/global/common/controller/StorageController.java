package com.navisa.be.global.common.controller;

import com.navisa.be.global.common.service.StorageService;
import com.navisa.be.global.web.response.BaseResponse;
import com.navisa.be.global.common.dto.request.IssuedPresignedUrlRequest;
import com.navisa.be.global.common.dto.response.PresignedUrlResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Storage",
        description = "원격 저장소 관련 API"
)
@RequiredArgsConstructor
@RequestMapping("/api/storage")
@RestController
public class StorageController {

    private final StorageService storageService;

    @Operation(
            summary = "presigned URL 발급 API",
            description = "자세한 설명은 다음 링크 참고 https://www.notion.so/bside/presigned-URL-2f4220202735802aaecbff2ed009c46c?source=copy_link"
    )
    @PostMapping("/presigned-url")
    public BaseResponse<PresignedUrlResponse> issuePresignedURL(@RequestBody @Valid IssuedPresignedUrlRequest request) {
        return new BaseResponse<>(storageService.issuePresignedUrl(request));
    }
}

package com.navisa.be.agent.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "행정사 프로필 수정 요청 DTO")
public record AgentProfileUpdateRequest(
        @Schema(description = "새로운 프로필 이미지 S3 키")
        String profileObjectKey,

        @Schema(description = "전화번호", example = "010-1234-5678")
        String phoneNumber,

        @Schema(description = "사무소명", example = "나비사 행정사 사무소")
        String officeName,

        @Schema(description = "영업시간", example = "평일 09:00 ~ 18:00")
        String businessHours,

        @Schema(description = "도로명주소", example = "서울특별시 강남구 테헤란로 123")
        String roadAddress,

        @Schema(description = "상세주소", example = "나비빌딩 5층 501호")
        String officeAddressDetail,

        @Schema(description = "전문분야 직무코드 ID 리스트", example = "[1, 2, 3]")
        List<Long> specializedJobCodeIdList,

        @Schema(description = "사용가능 언어 ID 리스트", example = "[1, 2, 3]")
        List<Long> availableLanguageIdList,

        @Schema(description = "행정사 한마디", example = "친절하게 상담해 드립니다.")
        String introduction,

        @Schema(description = "추가 이력", example = "법무부 출입국 관리소 10년 근무")
        String additionalCareer
) {
}

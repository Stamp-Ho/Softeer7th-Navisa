package com.navisa.be.agent.dto.request;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.user.model.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "행정사 등록 API 요청 DTO")
public record AgentProfileRegistrationRequest(@Valid @NotNull(message = "null일 수 없습니다") AgentBasicInfoDto basicInfo,
                                              @Valid @NotNull(message = "null일 수 없습니다") LicenseInfoDto licenseInfo,
                                              @Valid @NotNull(message = "null일 수 없습니다") DetailedInfoDto detailedInfo) {

    public AgentProfile dtoToEntity(User user) {
        return new AgentProfile(basicInfo().agentName(),
                basicInfo().birthDate(),
                basicInfo().profileImageUrl(),
                basicInfo().businessTime(),
                basicInfo().officeName(),
                basicInfo().officeAddress(),
                basicInfo().officeAddressDetail(),
                detailedInfo().additionalHistory(),
                basicInfo().phoneNumber(),
                user.getId(),
                licenseInfo().licenseNo(),
                licenseInfo().licenseIssuedAt(),
                licenseInfo().licenseInnerPageNo(),
                licenseInfo().licenseManagementNo(),
                detailedInfo().agentComment()
        );
    }

    @Schema(description = "기본 정보")
    public record AgentBasicInfoDto(@Schema(description = "프로필 이미지") @NotBlank String profileImageUrl,
                                    @Schema(description = "행정사 이름") @NotBlank String agentName,
                                    @Schema(description = "생년월일") @NotNull LocalDate birthDate,
                                    @Schema(description = "사무실 이름") @NotBlank String officeName,
                                    @Schema(description = "사무실 주소") @NotBlank String officeAddress,
                                    @Schema(description = "사무실 세부 주소") @NotNull String officeAddressDetail,
                                    @Schema(description = "행정사 영업시간") @NotBlank String businessTime,
                                    @Schema(description = "행정사 전화번호") @NotBlank String phoneNumber) {

    }

    @Schema(description = "자격증 정보")
    public record LicenseInfoDto(@Schema(description = "자격증 번호") String licenseNo,
                                 @Schema(description = "자격증 발급 연월일") LocalDate licenseIssuedAt,
                                 @Schema(description = "자격증 내지번호") String licenseInnerPageNo,
                                 @Schema(description = "자격증 관리번호") String licenseManagementNo) {

    }

    @Schema(description = "세부 정보")
    public record DetailedInfoDto(@Schema(description = "특화 직무 코드 id 리스트") @NotNull List<Long> specializedJobCodeIdList,
                                  @Schema(description = "사용 가능 언어 id 리스트") @NotNull List<Long> availableLanguageIdList,
                                  @Schema(description = "행정사 한마디") @NotBlank String agentComment,
                                  @Schema(description = "추가 이력") String additionalHistory) {

    }
}


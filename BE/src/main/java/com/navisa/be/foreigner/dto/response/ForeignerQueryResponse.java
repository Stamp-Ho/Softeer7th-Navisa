package com.navisa.be.foreigner.dto.response;

import com.navisa.be.foreigner.model.entity.ForeignerCareers;
import com.navisa.be.foreigner.model.entity.ForeignerEducation;
import com.navisa.be.foreigner.model.entity.ForeignerExpectedCompany;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.model.enums.EducationDegreeLevel;
import com.navisa.be.foreigner.model.enums.ForeignerSearchStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "외국인 프로필 수정 폼용 전체 데이터 응답")
public record ForeignerQueryResponse(
        @Schema(description = "외국인 이름")
        String name,

        @Schema(description = "국적 ID 리스트", example = "[1, 2]")
        List<Long> nationIdList,

        @Schema(description = "언어 ID 리스트", example = "[10, 15]")
        List<Long> languageIdList,

        @Schema(description = "학력 정보")
        EducationInfo education,

        @Schema(description = "경력 리스트")
        List<CareerInfo> foreignerCareers,

        @Schema(description = "희망 회사/직무 정보")
        ExpectedCompanyInfo expectedCompany,

        @Schema(description = "구직 요청 여부", example = "true")
        boolean isRequesting
) {
    public static ForeignerQueryResponse of(
            ForeignerProfile profile,
            ForeignerEducation edu,
            List<ForeignerCareers> careers,
            ForeignerExpectedCompany expected
    ) {
        return new ForeignerQueryResponse(
                profile.getNickname(),
                profile.getForeignerNationalities().stream()
                        .map(n -> n.getNationality().getId()).toList(),
                profile.getForeignLanguages().stream()
                        .map(l -> l.getLanguage().getId()).toList(),
                new EducationInfo(edu.getDegreeLevel(), edu.getSchoolName(), edu.getMajorName()),
                careers.stream()
                        .map(c -> new CareerInfo(c.getCompanyName(), c.getJobTitle(), c.getStartDate(), c.getEndDate(), c.isWork()))
                        .toList(),
                new ExpectedCompanyInfo(expected.getCompanyName(), expected.getJobTitle(), expected.getStartDate()),
                profile.getStatus() == ForeignerSearchStatus.REQUESTING
        );
    }

    @Schema(description = "상세 학력 정보")
    public record EducationInfo(
            @Schema(description = "학위 단계") EducationDegreeLevel degreeLevel,
            @Schema(description = "학교명") String schoolName,
            @Schema(description = "전공명") String majorName
    ) {}

    @Schema(description = "상세 경력 정보")
    public record CareerInfo(
            @Schema(description = "회사명") String companyName,
            @Schema(description = "직무 타이틀") String jobTitle,
            @Schema(description = "시작일") LocalDate startDate,
            @Schema(description = "종료일 (재직 중일 경우 null)") LocalDate endDate,
            @Schema(description = "현재 재직 여부") boolean isWork
    ) {}

    @Schema(description = "상세 입사 예정 근무지 정보")
    public record ExpectedCompanyInfo(
            @Schema(description = "입사 예정 회사명") String companyName,
            @Schema(description = "입사 예정 직무") String jobTitle,
            @Schema(description = "입사 가능일") LocalDate startDate
    ) {}
}

package com.navisa.be.foreigner.dto.response;

import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.foreigner.model.entity.ForeignerCareers;
import com.navisa.be.foreigner.model.entity.ForeignerEducation;
import com.navisa.be.foreigner.model.entity.ForeignerExpectedCompany;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.model.enums.EducationDegreeLevel;
import com.navisa.be.user.model.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Schema(description = "외국인 상세 조회 dto")
public record ForeignerDetailResponse(
        ForeignerBasicInfo basicInfo,
        EducationInfo educationInfo,
        @Schema(description = "언어 id 리스트")
        List<Long> languageList,
        CareerInfo careerInfo,
        ExpectedCompanyInfo expectedCompanyInfo
) {
    @Schema(description = "최종 학력 dto")
    public record EducationInfo(
            @Schema(description = "학위 단계")
            EducationDegreeLevel degreeLevel,
            @Schema(description = "대학명")
            String school,
            @Schema(description = "전공명")
            String major
    ) {
        public static EducationInfo of(ForeignerEducation edu) {
            return new EducationInfo(edu.getDegreeLevel(), edu.getSchoolName(), edu.getMajorName());
        }
    }

    @Schema(description = "경력 dto")
    public record CareerInfo(
            @Schema(description = "총 경력 개월수")
            Integer totalCareerMonths,
            List<CareerInfo.CareerHistory> history
    ) {

        public static CareerInfo of(List<ForeignerCareers> careers) {
            // 외국인의 총 경력 개월 수 계산
            // 아래 연산은 리스트가 비어 있으면 0을 반환한다
            int totalCareerMonths = careers.stream()
                    .mapToInt(ForeignerCareers::getDurationMonths)
                    .sum();

            List<CareerInfo.CareerHistory> histories = careers.stream()
                    .map(career -> {
                        return new CareerInfo.CareerHistory(
                                career.getCompanyName(),
                                career.getJobTitle(),
                                career.getFormattedPeriod(),
                                career.getDurationMonths()
                        );
                    })
                    .toList();

            return new CareerInfo(totalCareerMonths, histories);
        }

        @Schema(description = "경력 히스토리")
        public record CareerHistory(
                @Schema(description = "회사명")
                String companyName,
                @Schema(description = "직무명")
                String jobTitle,
                @Schema(description = "기간")
                String period,
                @Schema(description = "개월수")
                Integer durationMonths
        ) {
        }
    }

    @Schema(description = "입사 예정 정보 dto")
    public record ExpectedCompanyInfo(
            @Schema(description = "직무")
            String targetJob,
            @Schema(description = "회사명")
            String companyName,
            @Schema(description = "시작일")
            LocalDate startDate
    ) {

        public static ExpectedCompanyInfo of(ForeignerExpectedCompany expectedCompany) {
            return new ExpectedCompanyInfo(
                    expectedCompany.getJobTitle(),
                    expectedCompany.getCompanyName(),
                    expectedCompany.getStartDate()
            );
        }
    }

    @Schema(description = "기본 정보 dto")
    public record ForeignerBasicInfo(
            @Schema(description = "외국인 id")
            UUID foreignerId,
            @Schema(description = "닉네임")
            String nickname,
            @Schema(description = "국적 id 리스트")
            List<Long> nationIdList,
            @Schema(description = "최근 접속일시")
            ZonedDateTime lastAccessDay,
            @Schema(description = "행정사와 외국인 사이의 채팅방 존재 여부")
            boolean hasChatRoomBetween,
            @Schema(description = "채팅방 id")
            Long chatRoomId
    ) {

        public static ForeignerBasicInfo of(ForeignerProfile foreignerProfile, List<Long> nationIds,Optional<ChatRoom> optChatRoom) {
            return new ForeignerBasicInfo(
                    foreignerProfile.getId(),
                    foreignerProfile.getNickname(),
                    nationIds,
                    foreignerProfile.getLastLoginAt(),
                    optChatRoom.isPresent(),
                    optChatRoom.map(ChatRoom::getId).orElse(null)
            );
        }
    }

}

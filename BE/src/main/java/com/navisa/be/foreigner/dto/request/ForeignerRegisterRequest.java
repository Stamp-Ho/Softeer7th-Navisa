package com.navisa.be.foreigner.dto.request;

import com.navisa.be.foreigner.dto.ForeignerCareerDto;
import com.navisa.be.foreigner.dto.ForeignerEducationDto;
import com.navisa.be.foreigner.dto.ForeignerExpectedCompanyDto;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.model.enums.ForeignerSearchStatus;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record ForeignerRegisterRequest(
        @NotNull(message = "nationIdList는 null이면 안됩니다.") List<Long> nationIdList,
        @NotNull(message = "languageIdList는 null이면 안됩니다.") List<Long> languageIdList,
        @NotNull(message = "foreignerEducation은 null이면 안됩니다.") ForeignerEducationDto education,
        @NotNull(message = "foreignerCareers는 null이면 안됩니다.") List<ForeignerCareerDto> foreignerCareers,
        @NotNull(message = "expectedCompany는 null이면 안됩니다.") ForeignerExpectedCompanyDto expectedCompany,
        @NotNull(message = "isIdle 값은 null이면 안됩니다.") Boolean isIdle) {

    public ForeignerProfile toProfileEntity(UUID userId) {
        return new ForeignerProfile(userId, isIdle ? ForeignerSearchStatus.IDLE : ForeignerSearchStatus.REQUESTING);
    }
}

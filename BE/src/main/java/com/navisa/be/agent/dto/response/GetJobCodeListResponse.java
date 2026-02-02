package com.navisa.be.agent.dto.response;

import com.navisa.be.agent.dto.JobCodeDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "직무코드 조회 응답 DTO")
public record GetJobCodeListResponse(
        List<JobCodeDto> jobCodeList
) {
}

package com.navisa.be.global.common.service;

import com.navisa.be.agent.dto.projection.JobCodeProjection;
import com.navisa.be.agent.dto.response.JobCodeListResponse;
import com.navisa.be.global.common.repository.JobCodeRepository;
import com.navisa.be.global.common.service.JobCodeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobCodeServiceTest {

    @InjectMocks
    private JobCodeService jobCodeService;

    @Mock
    private JobCodeRepository jobCodeRepository;

    @Test
    @DisplayName("직무코드 조회는 결과를 반환한다")
    void getJobCodeList_returnResult_whenJobCodesExist() {
        // given
        List<JobCodeProjection> dtos = List.of(new JobCodeProjection(1L, "직무1", "code1"), new JobCodeProjection(2L,"직무2", "code2"));
        when(jobCodeRepository.findAllJobCodeDtos()).thenReturn(dtos);

        // when
        JobCodeListResponse response = jobCodeService.getJobCodeList();

        // then
        assertThat(response.jobCodeList()).hasSize(2);
        assertThat(response.jobCodeList().get(0).jobCodeId()).isEqualTo(1L);
        assertThat(response.jobCodeList().get(0).code()).isEqualTo("code1");
        assertThat(response.jobCodeList().get(0).name()).isEqualTo("직무1");
    }

    @Test
    @DisplayName("직무코드가 존재하지 않으면 직무코드 조회는 빈 리스트를 반환한다")
    void getJobCodeList_returnEmptyList_whenNoJobCode() {
        // given
        List<JobCodeProjection> dtos = List.of();
        when(jobCodeRepository.findAllJobCodeDtos()).thenReturn(dtos);

        // when
        JobCodeListResponse response = jobCodeService.getJobCodeList();

        // then
        assertThat(response.jobCodeList()).isEmpty();
    }

}
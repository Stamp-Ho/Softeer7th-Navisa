package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.JobCodeDto;
import com.navisa.be.agent.dto.response.GetJobCodeListResponse;
import com.navisa.be.global.common.repository.JobCodeRepository;
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
        List<JobCodeDto> dtos = List.of(new JobCodeDto(1L, "직무1", "code1"), new JobCodeDto(2L,"직무2", "code2"));
        when(jobCodeRepository.findAllJobCodeDtos()).thenReturn(dtos);

        // when
        GetJobCodeListResponse response = jobCodeService.getJobCodeList();

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
        List<JobCodeDto> dtos = List.of();
        when(jobCodeRepository.findAllJobCodeDtos()).thenReturn(dtos);

        // when
        GetJobCodeListResponse response = jobCodeService.getJobCodeList();

        // then
        assertThat(response.jobCodeList()).isEmpty();
    }

}
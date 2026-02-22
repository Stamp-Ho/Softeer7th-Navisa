package com.navisa.be.support;

import com.navisa.be.agent.dto.request.AgentProfileRegistrationRequest;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.model.entity.AgentSpecializedJob;
import com.navisa.be.global.common.model.entity.JobCode;
import org.springframework.stereotype.Component;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class AgentFixture {
    public static AgentProfileRegistrationRequest createAgentProfileRegistrationRequest(List<Long> jobIds, List<Long> langIds, String licenseNo, String mgmtNo) {
        return new AgentProfileRegistrationRequest(
                new AgentProfileRegistrationRequest.AgentBasicInfoDto("https://img.com/p.jpg",
                        "박행정",
                        LocalDate.of(1990, 1, 1),
                        "박행정 행정사사무소",
                        "서울시 종로구",
                        "101호",
                        "9:00 ~ 22:00",
                        "010-1234-1234"),
                new AgentProfileRegistrationRequest.LicenseInfoDto(licenseNo,
                        LocalDate.now(),
                        "Page-10",
                        mgmtNo),
                new AgentProfileRegistrationRequest.DetailedInfoDto(jobIds,
                        langIds,
                        "법무부 등록 대행 기관입니다.",
                        "비자 발급률 99%를 자랑합니다.")
        );
    }

    public static AgentProfileRegistrationRequest getAgentProfileRegistrationRequestConsistingOfNull() {
        AgentProfileRegistrationRequest request = new AgentProfileRegistrationRequest(
                null,
                null,
                null
        );
        return request;
    }

    public static AgentProfileRegistrationRequest createAgentProfileRegistrationRequest(List<Long> jobIds, List<Long> langIds, AgentProfileRegistrationRequest.LicenseInfoDto licenseInfoDto){
        return new AgentProfileRegistrationRequest(
                new AgentProfileRegistrationRequest.AgentBasicInfoDto("https://img.com/p.jpg",
                        "박행정",
                        LocalDate.of(1990, 1, 1),
                        "박행정 행정사사무소",
                        "서울시 종로구",
                        "101호",
                        "9:00 ~ 22:00",
                        "010-1234-1234"),
                licenseInfoDto,
                new AgentProfileRegistrationRequest.DetailedInfoDto(jobIds,
                        langIds,
                        "법무부 등록 대행 기관입니다.",
                        "비자 발급률 99%를 자랑합니다.")
        );
    }

    /**
     * 추천 로직 테스트를 위한 AgentProfile 엔티티 Fixture
     */
    public static AgentProfile createAgentProfile(UUID id, double activeScore) {
        AgentProfile profile = new AgentProfile(
                "박행정",
                LocalDate.of(1990, 1, 1),
                "profile-key-" + id,
                "09:00~18:00",
                "박행정사무소",
                "서울시 종로구",
                "101호",
                "경력 10년",
                "010-1234-5678",
                UUID.randomUUID(), // userId
                "L-123",
                LocalDate.now(),
                "P-10",
                "M-100",
                "안녕하세요"
        );

        ReflectionTestUtils.setField(profile, "id", id);
        ReflectionTestUtils.setField(profile, "activeScore", activeScore);

        JobCode jobCode = new JobCode(1L, "VISA_001", "비자 업무", new float[512], null);
        AgentSpecializedJob specializedJob = new AgentSpecializedJob(profile, jobCode);

        profile.addSpecializedJobCodes(List.of(specializedJob));

        return profile;
    }
}

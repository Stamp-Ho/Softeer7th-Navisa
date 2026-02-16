package com.navisa.be.support;

import com.navisa.be.agent.dto.request.AgentProfileRegistrationRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

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
}

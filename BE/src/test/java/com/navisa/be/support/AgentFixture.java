package com.navisa.be.support;

import com.navisa.be.agent.dto.BasicInfoDto;
import com.navisa.be.agent.dto.DetailedInfoDto;
import com.navisa.be.agent.dto.LicenseInfoDto;
import com.navisa.be.agent.dto.request.RegisterAgentProfileCommand;
import com.navisa.be.agent.dto.request.RegisterAgentProfileRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class AgentFixture {
    public static RegisterAgentProfileCommand createRegisterAgentProfileCommand(String userEmail, List<Long> jobIds, List<Long> langIds, String licenseNo, String mgmtNo) {
        return new RegisterAgentProfileCommand(
                new BasicInfoDto("https://img.com/p.jpg",
                        "박행정",
                        LocalDate.of(1990, 1, 1),
                        "박행정 행정사사무소",
                        "서울시 종로구",
                        "101호",
                        "9:00 ~ 22:00"),
                new LicenseInfoDto(licenseNo,
                        LocalDate.now(),
                        "Page-10",
                        mgmtNo),
                new DetailedInfoDto(jobIds,
                        langIds,
                        "법무부 등록 대행 기관입니다.",
                        "비자 발급률 99%를 자랑합니다."),
                userEmail
        );
    }

    public static RegisterAgentProfileRequest getRegisterAgentProfileRequestConsistingOfNull() {
        RegisterAgentProfileRequest request = new RegisterAgentProfileRequest(
                null,
                null,
                null
        );
        return request;
    }

    public static RegisterAgentProfileRequest createRegisterAgentProfileRequest(List<Long> jobIds, List<Long> langIds, LicenseInfoDto licenseInfoDto){
        return new RegisterAgentProfileRequest(
                new BasicInfoDto("https://img.com/p.jpg",
                        "박행정",
                        LocalDate.of(1990, 1, 1),
                        "박행정 행정사사무소",
                        "서울시 종로구",
                        "101호",
                        "9:00 ~ 22:00"),
                licenseInfoDto,
                new DetailedInfoDto(jobIds,
                        langIds,
                        "법무부 등록 대행 기관입니다.",
                        "비자 발급률 99%를 자랑합니다.")
        );
    }
}

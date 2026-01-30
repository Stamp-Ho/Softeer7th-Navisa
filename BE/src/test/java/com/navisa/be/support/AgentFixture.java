package com.navisa.be.support;

import com.navisa.be.agent.dto.BasicInfoDto;
import com.navisa.be.agent.dto.DetailedInfoDto;
import com.navisa.be.agent.dto.LicenseInfoDto;
import com.navisa.be.agent.dto.request.RegisterAgentProfileCommand;
import com.navisa.be.agent.dto.request.RegisterAgentProfileRequest;

import java.time.LocalDate;
import java.util.List;

public class AgentFixture {

    public static RegisterAgentProfileRequest getRegisterAgentProfileRequest() {
        var basicInfo = new BasicInfoDto(
                "https://example.com/profiles/agent_01.jpg",
                "홍길동",
                LocalDate.of(1985, 5, 20),
                "길동 행정사 사무소",
                "서울특별시 강남구 테헤란로 123",
                "4층 402호",
                "9:00 ~ 18:00"
        );


        var licenseInfo = new LicenseInfoDto(
                "2023-ABC-1234",
                LocalDate.of(2023, 10, 15),
                "P-98765",
                null
        );


        var detailedInfo = new DetailedInfoDto(
                List.of(1L, 3L, 5L), // 특화 직무 코드 ID 리스트
                List.of(10L, 20L),    // 사용 가능 언어 ID 리스트
                "고객의 권익을 최우선으로 생각하는 행정사 홍길동입니다.",
                "전) OO구청 인허가 담당 사무관 10년 근무"
        );

        RegisterAgentProfileRequest request = new RegisterAgentProfileRequest(
                basicInfo,
                licenseInfo,
                detailedInfo
        );
        return request;
    }

    public static RegisterAgentProfileCommand createRegisterAgentProfileCommand(String email, List<Long> jobIds, List<Long> langIds, String licenseNo, String mgmtNo) {
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
                email
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
}

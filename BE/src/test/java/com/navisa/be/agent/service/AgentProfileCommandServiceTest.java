package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.request.RegisterAgentProfileCommand;
import com.navisa.be.agent.exception.AgentException;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.repository.AgentLanguageRepository;
import com.navisa.be.agent.repository.AgentSpecializedJobRepository;
import com.navisa.be.common.model.entity.JobCode;
import com.navisa.be.common.model.entity.Language;
import com.navisa.be.common.repository.JobCodeRepository;
import com.navisa.be.common.repository.LanguageRepository;
import com.navisa.be.agent.service.AgentProfileCommandService;
import com.navisa.be.support.AgentFixture;
import com.navisa.be.support.IntegrationTestSupport;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.LoginType;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
class AgentProfileCommandServiceTest extends IntegrationTestSupport {

    @Autowired
    private AgentProfileCommandService agentProfileCommandService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobCodeRepository jobCodeRepository;

    @Autowired
    private AgentSpecializedJobRepository specializedJobCodeRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private AgentLanguageRepository agentLanguageRepository;

    private User scrivenerUser;

    private List<JobCode> jobCodes;

    private List<Language> languages;

    @BeforeEach
    void setUp() {
        scrivenerUser = userRepository
                .save(new User("visa_helper@example.com", "hash", UserType.UNVALID_AGENT, LoginType.EMAIL, true));

        jobCodes = jobCodeRepository.saveAll(List.of(
                new JobCode(null, "code1", "직종1", new float[512], null),
                new JobCode(null, "code2", "직종2", new float[512], null),
                new JobCode(null, "code3", "직종3", new float[512], null),
                new JobCode(null, "code4", "직종4", new float[512], null)));

        languages = languageRepository.saveAll(List.of(
                new Language(null, "한국어"),
                new Language(null, "영어"),
                new Language(null, "일본어"),
                new Language(null, "중국어")));
    }

    @Test
    @DisplayName("행정사가 전문 직무를 선택하여 프로필을 등록한다")
    void register_shouldAgentProfile_WithVisaCodes() {
        // given
        List<Long> selectedJobCodeIds = List.of(jobCodes.get(0).getId(), jobCodes.get(1).getId());
        List<Long> selectedLangIds = List.of(languages.get(0).getId(), languages.get(1).getId());

        // validateCommand를 통과하는 요청
        RegisterAgentProfileCommand command = AgentFixture.createRegisterAgentProfileCommand(
                scrivenerUser.getEmail(),
                selectedJobCodeIds,
                selectedLangIds,
                "2024-행정-1234",
                null);

        // when
        AgentProfile result = agentProfileCommandService.registerAgentProfile(command);

        // then
        assertAll(
                () -> Assertions.assertNotNull(result.getId()),
                () -> Assertions.assertEquals(command.basicInfo().officeName(), result.getOfficeName()),
                () -> Assertions.assertEquals(scrivenerUser.getId(), result.getUserId()),
                () -> Assertions.assertEquals(command.basicInfo().businessTime(), result.getBusinessTime()),
                () -> {
                    // 전문 직무 저장 확인
                    long mappingCount = specializedJobCodeRepository.countByAgentProfile(result);
                    Assertions.assertEquals(2L, mappingCount);
                },
                () -> {
                    // 사용 가능 언어 저장 확인
                    long mappingCount = agentLanguageRepository.countByAgentProfile(result);
                    Assertions.assertEquals(2L, mappingCount);
                });
    }

    @Test
    @DisplayName("자격증 번호와 관리 번호가 모두 존재하면 등록에 실패한다")
    void register_shouldFail_whenBothLicenseNumbersExist() {
        // given

        // validateCommand에서 예외가 발생하는 요청
        RegisterAgentProfileCommand command = AgentFixture.createRegisterAgentProfileCommand(
                scrivenerUser.getEmail(),
                List.of(jobCodes.get(0).getId()),
                List.of(languages.get(0).getId()),
                "LICENSE-123",
                "MGMT-999");

        // when & then
        assertThrows(AgentException.class, () -> agentProfileCommandService.registerAgentProfile(command));
    }

    @Test
    @DisplayName("등록되지 않은 직무코드가 포함되면 등록에 실패한다")
    void register_shouldFail_whenInvalidJobCode() {
        // given

        // 존재하지 않는 id
        List<Long> invalidIds = List.of(999L);
        RegisterAgentProfileCommand command = AgentFixture.createRegisterAgentProfileCommand(
                scrivenerUser.getEmail(),
                invalidIds,
                List.of(languages.get(0).getId()),
                "L-1",
                null);

        // when & then
        assertThrows(AgentException.class, () -> agentProfileCommandService.registerAgentProfile(command));
    }

    @Test
    @DisplayName("등록되지 않은 언어가 포함되면 등록에 실패한다")
    void register_shouldFail_whenInvalidLanguage() {
        // given

        // 존재하지 않는 id
        List<Long> invalidIds = List.of(999L);
        RegisterAgentProfileCommand command = AgentFixture.createRegisterAgentProfileCommand(
                scrivenerUser.getEmail(),
                List.of(jobCodes.get(0).getId()),
                invalidIds,
                "L-1",
                null);

        // when & then
        assertThrows(AgentException.class, () -> agentProfileCommandService.registerAgentProfile(command));
    }
}
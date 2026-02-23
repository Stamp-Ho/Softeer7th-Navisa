package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.request.AgentProfileRegistrationRequest;
import com.navisa.be.agent.dto.request.AgentProfileUpdateRequest;
import com.navisa.be.agent.dto.response.AgentDetailResponse;
import com.navisa.be.agent.exception.AgentException;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.repository.AgentLanguageRepository;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.agent.repository.AgentSpecializedJobRepository;
import com.navisa.be.global.common.model.entity.JobCode;
import com.navisa.be.global.common.model.entity.Language;
import com.navisa.be.global.common.repository.JobCodeRepository;
import com.navisa.be.global.common.repository.LanguageRepository;
import com.navisa.be.support.AgentFixture;
import com.navisa.be.support.IntegrationTestSupport;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.LoginType;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
class AgentProfileRegistrationServiceTest extends IntegrationTestSupport {

    @Autowired
    private AgentProfileRegistrationService agentProfileRegistrationService;

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

    @Autowired
    private AgentProfileRepository agentProfileRepository;

    private User scrivenerUser;

    private List<JobCode> jobCodes;

    private List<Language> languages;

    @BeforeEach
    void setUp() {
        scrivenerUser = userRepository
                .save(new User("visa_helper@example.com", "hash", UserType.INVALID_AGENT, LoginType.EMAIL, true));

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
        AgentProfileRegistrationRequest request = AgentFixture.createAgentProfileRegistrationRequest(
                selectedJobCodeIds,
                selectedLangIds,
                "2024-행정-1234",
                null);

        // when
        AgentProfile result = agentProfileRegistrationService.registerAgentProfile(request, scrivenerUser.getEmail());

        // then
        User user = userRepository.findById(scrivenerUser.getId()).orElseThrow();

        assertAll(
                () -> assertNotNull(result.getId()),
                () -> assertEquals(request.basicInfo().officeName(), result.getOfficeName()),
                () -> assertEquals(scrivenerUser.getId(), result.getUserId()),
                () -> assertEquals(request.basicInfo().businessTime(), result.getBusinessTime()),
                () -> {
                    // 전문 직무 저장 확인
                    long mappingCount = specializedJobCodeRepository.countByAgentProfile(result);
                    assertEquals(2L, mappingCount);
                },
                () -> {
                    // 사용 가능 언어 저장 확인
                    long mappingCount = agentLanguageRepository.countByAgentProfile(result);
                    assertEquals(2L, mappingCount);
                });

        // TODO :: 최종발표를 위해서 프로필을 등록하면 바로 VALID_AGENT가 되도록 수정
        Assertions.assertThat(user.getUserType()).isEqualTo(UserType.VALID_AGENT);
    }

    @Test
    @DisplayName("자격증 번호와 관리 번호가 모두 존재하면 등록에 실패한다")
    void register_shouldFail_whenBothLicenseNumbersExist() {
        // given

        // validateCommand에서 예외가 발생하는 요청
        AgentProfileRegistrationRequest request = AgentFixture.createAgentProfileRegistrationRequest(
                List.of(jobCodes.get(0).getId()),
                List.of(languages.get(0).getId()),
                "LICENSE-123",
                "MGMT-999");

        // when & then
        assertThrows(AgentException.class, () -> agentProfileRegistrationService.registerAgentProfile(request, scrivenerUser.getEmail()));
    }

    @Test
    @DisplayName("등록되지 않은 직무코드가 포함되면 등록에 실패한다")
    void register_shouldFail_whenInvalidJobCode() {
        // given

        // 존재하지 않는 id
        List<Long> invalidIds = List.of(999L);
        AgentProfileRegistrationRequest request = AgentFixture.createAgentProfileRegistrationRequest(
                invalidIds,
                List.of(languages.get(0).getId()),
                "L-1",
                null);

        // when & then
        assertThrows(AgentException.class, () -> agentProfileRegistrationService.registerAgentProfile(request, scrivenerUser.getEmail()));
    }

    @Test
    @DisplayName("등록되지 않은 언어가 포함되면 등록에 실패한다")
    void register_shouldFail_whenInvalidLanguage() {
        // given

        // 존재하지 않는 id
        List<Long> invalidIds = List.of(999L);
        AgentProfileRegistrationRequest request = AgentFixture.createAgentProfileRegistrationRequest(
                List.of(jobCodes.get(0).getId()),
                invalidIds,
                "L-1",
                null);

        // when & then
        assertThrows(AgentException.class, () -> agentProfileRegistrationService.registerAgentProfile(request, scrivenerUser.getEmail()));
    }

    @Test
    @DisplayName("행정사 프로필 정보를 수정하면 DB에 반영되고 상세 응답을 반환한다")
    void updateAgentProfile_shouldUpdateEntityAndReturnResponse() {
        // Given
        AgentProfile savedProfile = registerDefaultProfile();
        AgentProfileUpdateRequest updateRequest = new AgentProfileUpdateRequest(
                null, "010-9999-9999", "수정된 사무소", null, null,
                "수정된 상세주소 101호",
                List.of(jobCodes.get(1).getId(), jobCodes.get(2).getId()),
                List.of(languages.get(1).getId()),
                null, null
        );

        // When
        AgentDetailResponse response = agentProfileRegistrationService.updateAgentProfile(updateRequest, scrivenerUser.getEmail());

        // Then
        assertAll(
                () -> assertEquals("수정된 사무소", response.officeInfo().officeName()),
                () -> assertEquals("수정된 상세주소 101호", response.officeInfo().officeAddressDetail()),
                () -> assertEquals("박행정", response.agentInfo().name()),
                // 전문 직무 교체 확인 (1개 -> 2개)
                () -> assertEquals(2L, specializedJobCodeRepository.countByAgentProfile(savedProfile)),
                // 언어 교체 확인 (1개 -> 1개)
                () -> assertEquals(1L, agentLanguageRepository.countByAgentProfile(savedProfile))
        );
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 프로필 수정을 요청하면 예외가 발생한다")
    void updateAgentProfile_shouldFail_whenUserNotFound() {
        // given
        AgentProfileUpdateRequest request = new AgentProfileUpdateRequest(null, null, null, null, null, null, null, null, null, null);
        String nonExistentEmail = "unknown@example.com";

        // when & then
        assertThrows(AgentException.class, () ->
                agentProfileRegistrationService.updateAgentProfile(request, nonExistentEmail));
    }

    @Test
    @DisplayName("이미지 키를 포함하여 프로필을 수정하면 새로운 이미지 경로가 반영된다")
    void updateAgentProfile_includingImage_shouldUpdateSuccessfully() {
        // Given
        String oldImageKey = "old-image-key";
        registerProfileWithImage(oldImageKey);

        String newImageKey = "new-profile-image-uuid-key";
        AgentProfileUpdateRequest updateRequest = new AgentProfileUpdateRequest(
                newImageKey, null, "내비자 수정 사무소", null, null,
                "수정 상세주소",
                List.of(jobCodes.get(0).getId()),
                List.of(languages.get(0).getId()),
                null, null
        );

        // When
        AgentDetailResponse response = agentProfileRegistrationService.updateAgentProfile(updateRequest, scrivenerUser.getEmail());

        // Then
        assertAll(
                () -> assertEquals("박행정", response.agentInfo().name()),
                () -> assertThat(response.agentInfo().profileImageUrl()).contains(newImageKey),
                () -> {
                    AgentProfile updatedEntity = agentProfileRepository.findByUserEmail(scrivenerUser.getEmail()).orElseThrow();
                    assertEquals(newImageKey, updatedEntity.getProfileObjectKey());
                }
        );
    }

    private AgentProfile registerDefaultProfile() {
        AgentProfileRegistrationRequest request = AgentFixture.createAgentProfileRegistrationRequest(
                List.of(jobCodes.get(0).getId()),
                List.of(languages.get(0).getId()),
                "2024-행정-1234",
                null);
        return agentProfileRegistrationService.registerAgentProfile(request, scrivenerUser.getEmail());
    }

    private void registerProfileWithImage(String imageKey) {
        AgentProfileRegistrationRequest.AgentBasicInfoDto basicInfo = new AgentProfileRegistrationRequest.AgentBasicInfoDto(
                imageKey, "박행정", LocalDate.of(1990, 1, 1), "박행정 사무소", "서울", "101호", "9-18", "010-1234"
        );
        AgentProfileRegistrationRequest request = new AgentProfileRegistrationRequest(
                basicInfo,
                new AgentProfileRegistrationRequest.LicenseInfoDto("2024-행정-1234", LocalDate.now(), "P-10", null),
                new AgentProfileRegistrationRequest.DetailedInfoDto(List.of(jobCodes.get(0).getId()), List.of(languages.get(0).getId()), "comment", "history")
        );
        agentProfileRegistrationService.registerAgentProfile(request, scrivenerUser.getEmail());
    }
}
package com.navisa.be.application.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.application.dto.request.VisaApplicationSaveRequest;
import com.navisa.be.application.dto.response.VisaApplicationSaveResponse;
import com.navisa.be.application.exception.ApplicationException;
import com.navisa.be.application.model.entity.VisaApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.common.model.entity.JobCode;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.common.repository.JobCodeRepository;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.repository.ForeignerProfileRepository;
import com.navisa.be.support.IntegrationTestSupport;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.LoginType;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class ApplicationCommandServiceTest extends IntegrationTestSupport {

    @Autowired
    private ApplicationCommandService applicationCommandService;

    @Autowired
    private ApplicationFormRepository visaApplicationFormRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AgentProfileRepository agentProfileRepository;

    @Autowired
    private ForeignerProfileRepository foreignerProfileRepository;

    @Autowired
    private JobCodeRepository jobCodeRepository;

    @Autowired
    private EntityManager em;

    @DisplayName("비자 신청서의 특정 섹션 데이터를 전송하면 해당 섹션만 업데이트(Upsert)된다.")
    @Test
    void saveVisaForm_UpsertSection_Success() {
        // given
        String email = "test@navisa.com";
        User loginUser = saveUser(email, UserType.VALID_AGENT);

        VisaApplicationForm form = setupInitialForm(loginUser);
        UUID formId = form.getId();

        // 1번 섹션(인적사항)과 2번 섹션(여권 정보) 데이터
        Map<String, Object> section1 = Map.of(
                "sectionId", 1,
                "fields", List.of(Map.of("fieldId", 101, "value", "Gildong Hong"))
        );
        Map<String, Object> section2 = Map.of(
                "sectionId", 2,
                "fields", List.of(Map.of("fieldId", 201, "value", "M12345678"))
        );

        VisaApplicationSaveRequest request = new VisaApplicationSaveRequest(150, 45, List.of(section1, section2));

        // when
        VisaApplicationSaveResponse response = applicationCommandService.saveVisaForm(email, formId, request);
        em.flush();
        em.clear();

        // then
        VisaApplicationForm updatedForm = visaApplicationFormRepository.findById(formId).get();

        assertThat(response.visaFormId()).isEqualTo(formId);
        assertThat(updatedForm.getTotalCount()).isEqualTo(150);
        assertThat(updatedForm.getCurrentStep()).isEqualTo(45);

        assertThat(updatedForm.getPersonalDetail()).containsEntry("sectionId", 1);
        assertThat(updatedForm.getPassportInformation()).containsEntry("sectionId", 2);

        assertThat(updatedForm.getEducation()).isNull();
    }

    @DisplayName("존재하지 않는 비자 신청서 ID로 저장 시도 시 예외가 발생한다.")
    @Test
    void saveVisaForm_NotFound_Fail() {
        // given
        String email = "test@navisa.com";
        saveUser(email, UserType.VALID_AGENT);
        UUID invalidId = UUID.randomUUID();
        VisaApplicationSaveRequest request = new VisaApplicationSaveRequest(100, 10, List.of());

        // when & then
        assertThatThrownBy(() -> applicationCommandService.saveVisaForm(email, invalidId, request))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining(ResponseStatus.VISA_APP_FORM_NOT_FOUND.getMessage());
    }

    @DisplayName("섹션 정보를 수정하면 엔티티의 updatedAt이 자동으로 갱신된다.")
    @Test
    void saveVisaForm_UpdatesTimestamp() {
        // given
        String email = "test@navisa.com";
        User loginUser = saveUser(email, UserType.VALID_AGENT);
        VisaApplicationForm form = setupInitialForm(loginUser);

        em.flush();
        em.clear();
        VisaApplicationForm savedForm = visaApplicationFormRepository.findById(form.getId()).get();
        String initialTime = savedForm.getUpdatedAt().toString();

        Map<String, Object> section1 = Map.of("sectionId", 1, "data", "new data");
        VisaApplicationSaveRequest request = new VisaApplicationSaveRequest(200, 20, List.of(section1));

        // when
        applicationCommandService.saveVisaForm(email, form.getId(), request);
        em.flush();
        em.clear();

        // then
        VisaApplicationForm updatedForm = visaApplicationFormRepository.findById(form.getId()).get();
        assertThat(updatedForm.getUpdatedAt().toString()).isNotEqualTo(initialTime);
    }

    @DisplayName("비자 신청서 ID를 통해 해당 외국인의 프로필 사진 경로를 저장할 수 있다.")
    @Test
    void saveProfilePhoto_Success() {
        // given
        String email = "agent@navisa.com";
        User loginUser = saveUser(email, UserType.VALID_AGENT);

        VisaApplicationForm form = setupInitialForm(loginUser);
        UUID visaFormId = form.getId();
        String newObjectKey = "foreigner-profile/origin/2026/02/10/new-photo.webp";

        // when
        VisaApplicationSaveResponse response = applicationCommandService.saveProfilePhoto(email, visaFormId, newObjectKey);

        em.flush();
        em.clear();

        // then
        assertThat(response.visaFormId()).isEqualTo(visaFormId);

        VisaApplicationForm updatedForm = visaApplicationFormRepository.findById(visaFormId).get();
        String savedKey = updatedForm.getProfileObjectKey();

        assertThat(savedKey).isEqualTo(newObjectKey);
    }

    @Test
    @DisplayName("비자 신청서의 작성 완료 상태(isDone)를 변경할 수 있다.")
    void updateApplicationStatus_Success() {
        // given
        String email = "agent@navisa.com";
        User loginUser = saveUser(email, UserType.VALID_AGENT);

        VisaApplicationForm form = setupInitialForm(loginUser);
        UUID visaFormId = form.getId();

        // when
        VisaApplicationSaveResponse response = applicationCommandService.updateApplicationStatus(email, visaFormId, true);

        em.flush();
        em.clear();

        // then
        assertThat(response.visaFormId()).isEqualTo(visaFormId);

        VisaApplicationForm updatedForm = visaApplicationFormRepository.findById(visaFormId).get();
        assertThat(updatedForm.getIsDone()).isTrue();
    }

    private VisaApplicationForm setupInitialForm(User owner) {
        AgentProfile agent = saveAgentProfile(owner.getId());
        User foreignerUser = saveUser("foreigner_owner@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreigner = foreignerProfileRepository.save(new ForeignerProfile(foreignerUser.getId(), null));
        JobCode jobCode = jobCodeRepository.save(new JobCode(null, "E7", "특수활동", null, null));

        return visaApplicationFormRepository.save(new VisaApplicationForm(agent, foreigner, jobCode, false, 100, 0));
    }

    private User saveUser(String email, UserType type) {
        return userRepository.save(new User(email, "pw", type, LoginType.EMAIL, true));
    }

    private AgentProfile saveAgentProfile(UUID userId) {
        return agentProfileRepository.save(new AgentProfile(
                "김행정", LocalDate.now(), "key", "09:00", "사무소", "주소", "상세", "이력",
                "010-1234-1234", userId, "LIC123", LocalDate.now(), "P123", "M123", "코멘트"));
    }
}

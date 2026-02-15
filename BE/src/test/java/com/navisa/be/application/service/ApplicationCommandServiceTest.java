package com.navisa.be.application.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.application.dto.request.VisaApplicationSaveRequest;
import com.navisa.be.application.dto.response.VisaApplicationFinishResponse;
import com.navisa.be.application.dto.response.VisaApplicationSaveResponse;
import com.navisa.be.application.exception.ApplicationException;
import com.navisa.be.application.model.entity.VisaApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.entity.Proposal;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import com.navisa.be.chat.model.enums.ProposalStatus;
import com.navisa.be.chat.repository.ChatRoomRepository;
import com.navisa.be.chat.repository.ProposalRepository;
import com.navisa.be.global.common.model.entity.JobCode;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.global.common.repository.JobCodeRepository;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.model.enums.ForeignerSearchStatus;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
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
    private ApplicationFormRepository applicationFormRepository;

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

    @Autowired
    private ProposalRepository proposalRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

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
        VisaApplicationForm updatedForm = applicationFormRepository.findById(formId).get();

        assertThat(response.visaFormId()).isEqualTo(formId);
        assertThat(updatedForm.getTotalCount()).isEqualTo(150);
        assertThat(updatedForm.getCurrentStep()).isEqualTo(45);

        assertThat(updatedForm.getPersonalDetail()).doesNotContainKey("sectionId");
        assertThat(updatedForm.getPersonalDetail()).containsKey("fields");

        assertThat(updatedForm.getPassportInformation()).doesNotContainKey("sectionId");
        assertThat(updatedForm.getPassportInformation()).containsKey("fields");

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
        VisaApplicationForm savedForm = applicationFormRepository.findById(form.getId()).get();
        String initialTime = savedForm.getUpdatedAt().toString();

        Map<String, Object> section1 = Map.of("sectionId", 1, "data", "new data");
        VisaApplicationSaveRequest request = new VisaApplicationSaveRequest(200, 20, List.of(section1));

        // when
        applicationCommandService.saveVisaForm(email, form.getId(), request);
        em.flush();
        em.clear();

        // then
        VisaApplicationForm updatedForm = applicationFormRepository.findById(form.getId()).get();
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

        VisaApplicationForm updatedForm = applicationFormRepository.findById(visaFormId).get();
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

        VisaApplicationForm updatedForm = applicationFormRepository.findById(visaFormId).get();
        assertThat(updatedForm.isDone()).isTrue();
    }

    @Test
    @DisplayName("수임 종료 시 기존 폼은 종료되고, 행정사 정보가 없는 새 폼이 생성된다.")
    void finishApplication_Service_Success() {
        // given
        VisaApplicationForm oldForm = saveInitialForm();
        UUID oldFormId = oldForm.getId();

        UUID userId = oldForm.getAgentProfile().getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ResponseStatus.INVALID_USER));

        String agentEmail = user.getEmail();

        // when
        VisaApplicationFinishResponse response = applicationCommandService.finishApplication(agentEmail, oldFormId, true);

        // then
        // 기존 폼 검증
        VisaApplicationForm updatedOldForm = applicationFormRepository.findById(oldFormId).get();
        assertThat(updatedOldForm.isFinished()).isTrue();

        // 새 폼 검증
        VisaApplicationForm newForm = applicationFormRepository.findById(response.newVisaFormId()).get();
        assertThat(newForm.getAgentProfile()).isNull(); // 행정사 null
        assertThat(newForm.isFinished()).isFalse();
        assertThat(newForm.getForeignerProfile().getId()).isEqualTo(oldForm.getForeignerProfile().getId());
        assertThat(newForm.getPersonalDetail()).isEqualTo(oldForm.getPersonalDetail());
    }

    @Test
    @DisplayName("외국인이 강제 종료를 요청할 때, 메일 발송 후 3일이 지나지 않았으면 예외가 발생한다.")
    void finishByForeigner_Fail_Before_3Days() {
        // given
        User foreignerUser = saveUser("foreigner@test.com", UserType.FILLED_FOREIGNER);
        VisaApplicationForm form = setupInitialFormWithAgent(foreignerUser);

        ReflectionTestUtils.setField(form, "mailSentAt", LocalDateTime.now().minusDays(1));
        applicationFormRepository.saveAndFlush(form);

        // when & then
        assertThatThrownBy(() -> applicationCommandService.finishByForeigner(foreignerUser.getEmail()))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining("3일이 지나야 강제 종료가 가능합니다");
    }

    @Test
    @DisplayName("외국인이 강제 종료를 요청할 때, 모든 조건이 충족되면 신청서와 제안서 상태가 변경되고 새 신청서가 생성된다.")
    void finishByForeigner_Success() {
        // given
        User foreignerUser = saveUser("foreigner@test.com", UserType.FILLED_FOREIGNER);
        VisaApplicationForm oldForm = setupInitialFormWithAgent(foreignerUser);
        ChatRoom chatRoom = setupChatRoomAndProposal(oldForm);

        ReflectionTestUtils.setField(oldForm, "mailSentAt", LocalDateTime.now().minusDays(4));
        applicationFormRepository.saveAndFlush(oldForm);

        // when
        VisaApplicationFinishResponse response = applicationCommandService.finishByForeigner(foreignerUser.getEmail());

        // then
        // 기존 신청서 종료 확인
        VisaApplicationForm updatedOldForm = applicationFormRepository.findById(oldForm.getId()).get();
        assertThat(updatedOldForm.isFinished()).isTrue();

        // 제안서 상태가 COMPLETED로 변경되었는지 확인
        Proposal proposal = proposalRepository.findFirstByChatRoomOrderByIdDesc(chatRoom).get();
        assertThat(proposal.getStatus()).isEqualTo(ProposalStatus.COMPLETED);

        // 새 신청서 생성 확인
        VisaApplicationForm nextForm = applicationFormRepository.findById(response.newVisaFormId()).get();
        assertThat(nextForm.getAgentProfile()).isNull();
        assertThat(nextForm.isFinished()).isFalse();
    }

    private VisaApplicationForm setupInitialForm(User owner) {
        AgentProfile agent = saveAgentProfile(owner.getId());
        User foreignerUser = saveUser("foreigner_owner@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreigner = foreignerProfileRepository.save(new ForeignerProfile(foreignerUser.getId(), null));
        JobCode jobCode = jobCodeRepository.save(new JobCode(null, "E7", "특수활동", null, null));

        return applicationFormRepository.save(new VisaApplicationForm(agent, foreigner, jobCode, false, 100, 0));
    }

    private User saveUser(String email, UserType type) {
        return userRepository.save(new User(email, "pw", type, LoginType.EMAIL, true));
    }

    private AgentProfile saveAgentProfile(UUID userId) {
        return agentProfileRepository.save(new AgentProfile(
                "김행정", LocalDate.now(), "key", "09:00", "사무소", "주소", "상세", "이력",
                "010-1234-1234", userId, "LIC123", LocalDate.now(), "P123", "M123", "코멘트"));
    }

    private VisaApplicationForm saveInitialForm() {
        User agentUser = userRepository.save(new User("agent@test.com", "pw", UserType.VALID_AGENT, LoginType.EMAIL, true));
        AgentProfile agent = agentProfileRepository.save(new AgentProfile(
                "김행정", LocalDate.now(), "key", "09:00", "사무소", "주소", "상세", "이력",
                "010-1234-1234", agentUser.getId(), "LIC123", LocalDate.now(), "P123", "M123", "코멘트"));

        User foreignerUser = userRepository.save(new User("foreigner@test.com", "pw", UserType.FILLED_FOREIGNER, LoginType.EMAIL, true));
        ForeignerProfile foreigner = foreignerProfileRepository.save(new ForeignerProfile(foreignerUser.getId(), ForeignerSearchStatus.REQUESTING));

        JobCode jobCode = jobCodeRepository.save(new JobCode(null, "E7", "특수활동", null, null));

        VisaApplicationForm form = new VisaApplicationForm(agent, foreigner, jobCode, false, 100, 1);

        Map<String, Object> sampleData = Map.of("name", "Gildong Hong", "sectionId", 1);
        ReflectionTestUtils.setField(form, "personalDetail", sampleData);

        return applicationFormRepository.save(form);
    }

    private ChatRoom setupChatRoomAndProposal(VisaApplicationForm form) {
        ChatRoom chatRoom = chatRoomRepository.save(new ChatRoom(
                form.getForeignerProfile(),
                form.getAgentProfile(),
                ChatRoomStatus.DEFAULT,
                ZonedDateTime.now()
        ));

        Proposal proposal = new Proposal(chatRoom, form.getAgentProfile().getUserId());
        ReflectionTestUtils.setField(proposal, "status", ProposalStatus.MATCHED);
        proposalRepository.save(proposal);

        return chatRoom;
    }

    private VisaApplicationForm setupInitialFormWithAgent(User foreignerUser) {
        User agentUser = saveUser("agent_" + UUID.randomUUID() + "@test.com", UserType.VALID_AGENT);
        AgentProfile agent = saveAgentProfile(agentUser.getId());

        ForeignerProfile foreigner = foreignerProfileRepository.save(
                new ForeignerProfile(foreignerUser.getId(), ForeignerSearchStatus.REQUESTING)
        );

        JobCode jobCode = jobCodeRepository.save(new JobCode(null, "E7", "특수활동", null, null));

        VisaApplicationForm form = new VisaApplicationForm(agent, foreigner, jobCode, false, 100, 1);

        Map<String, Object> sampleData = Map.of("name", "Gildong Hong", "sectionId", 1);
        ReflectionTestUtils.setField(form, "personalDetail", sampleData);

        return applicationFormRepository.save(form);
    }
}

package com.navisa.be.application.model.entity;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.global.common.model.entity.BaseEntity;
import com.navisa.be.global.common.model.entity.JobCode;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "visa_application_form")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class ApplicationForm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "application_form_id")
    private UUID id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = true)
    private AgentProfile agentProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "foreigner_id", nullable = false)
    private ForeignerProfile foreignerProfile;

    @Column(name = "profile_object_key")
    private String profileObjectKey;

    @Column(name = "is_done", nullable = false)
    private boolean isDone = false;

    @Column(name = "is_finished", nullable = false)
    private boolean isFinished = false;

    @Column(name = "exported_at")
    private LocalDateTime exportedAt;

    @Column(name = "mail_sent_at")
    private LocalDateTime mailSentAt;

    @Column(name = "total_count", nullable = false)
    private Integer totalCount = 91;

    @Column(name = "current_step", nullable = false)
    private Integer currentStep = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_code_id", nullable = true)
    private JobCode jobCode;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "personal_detail", columnDefinition = "json")
    private Map<String, Object> personalDetail;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "passport_information", columnDefinition = "json")
    private Map<String, Object> passportInformation;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "contact_information", columnDefinition = "json")
    private Map<String, Object> contactInformation;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "marital_status_and_family_details", columnDefinition = "json")
    private Map<String, Object> maritalStatusAndFamilyDetails;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "education", columnDefinition = "json")
    private Map<String, Object> education;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "employment", columnDefinition = "json")
    private Map<String, Object> employment;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "visit_information", columnDefinition = "json")
    private Map<String, Object> visitInformation;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "help_information", columnDefinition = "json")
    private Map<String, Object> helpInformation;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "invite_information", columnDefinition = "json")
    private Map<String, Object> inviteInformation;

    public ApplicationForm(ForeignerProfile foreignerProfile) {
        this.agentProfile = null;
        this.foreignerProfile = foreignerProfile;
        this.jobCode = null;
        this.isDone = false;
        this.isFinished = false;
        this.exportedAt = null;
        this.mailSentAt = null;
    }

    public ApplicationForm(AgentProfile agentProfile, ForeignerProfile foreignerProfile,
            JobCode jobCode, Boolean isDone, Integer totalCount, Integer currentStep) {
        this.agentProfile = agentProfile;
        this.foreignerProfile = foreignerProfile;
        this.jobCode = jobCode;
        this.isDone = isDone;
        this.totalCount = totalCount;
        this.currentStep = currentStep;
    }

    public void updateAgentProfile(AgentProfile agentProfile) {
        this.agentProfile = agentProfile;
    }

    public void updateSections(List<Map<String, Object>> sections, Integer totalCount, Integer currentStep) {
        this.totalCount = totalCount;
        this.currentStep = currentStep;

        if (sections == null) return;

        for (Map<String, Object> section : sections) {
            Object sectionIdObj = section.get("sectionId");
            if (!(sectionIdObj instanceof Number sectionIdNum)) {
                continue;
            }

            int sectionId = sectionIdNum.intValue();
            Map<String, Object> dataToSave = new HashMap<>(section);
            dataToSave.remove("sectionId");

            switch (sectionId) {
                case 1 -> this.personalDetail = dataToSave;
                case 2 -> this.passportInformation = dataToSave;
                case 3 -> this.contactInformation = dataToSave;
                case 4 -> this.maritalStatusAndFamilyDetails = dataToSave;
                case 5 -> this.education = dataToSave;
                case 6 -> this.employment = dataToSave;
                case 7 -> this.visitInformation = dataToSave;
                case 8 -> this.helpInformation = dataToSave;
                case 9 -> this.inviteInformation = dataToSave;
                default -> log.warn("정의되지 않은 sectionId 입니다: {}", sectionId);
            }
        }
    }

    public void updateStatus(boolean isDone) {
        this.isDone = isDone;
        if (isDone && this.exportedAt == null) {
            this.exportedAt = LocalDateTime.now();
        }
    }

    // 외국인 이미지 업데이트 메서드
    public void updateProfileImage(String profileObjectKey) {
        this.profileObjectKey = profileObjectKey;
    }

    // 수임 종료 처리 메서드
    public void updateFinish(boolean isFinished) {
        this.isFinished = isFinished;
    }

    // 행정사 확인 이메일 발송 시간 업데이트 메서드
    public void recordMailSentTime() {
        this.mailSentAt = LocalDateTime.now();
    }

    private static Map<String, Object> copySection(Map<String, Object> section) {
        if (section == null) return null;
        return new HashMap<>(section); // 새로운 Map 객체를 생성하여 데이터만 복사
    }

    // 기존 데이터를 기반으로 새 신청서 생성 메서드
    public static ApplicationForm createRenewalForm(ApplicationForm oldForm) {
        ApplicationForm nextForm = new ApplicationForm();

        // 초기화가 필요한 필드
        nextForm.agentProfile = null;
        nextForm.isDone = false;
        nextForm.isFinished = false;
        nextForm.exportedAt = null;
        nextForm.mailSentAt = null;

        // 기존 값 유지 필드
        nextForm.foreignerProfile = oldForm.getForeignerProfile();
        nextForm.jobCode = oldForm.getJobCode();
        nextForm.totalCount = oldForm.getTotalCount();
        nextForm.currentStep = oldForm.getCurrentStep();
        nextForm.profileObjectKey = oldForm.getProfileObjectKey();

        nextForm.personalDetail = copySection(oldForm.getPersonalDetail());
        nextForm.passportInformation = copySection(oldForm.getPassportInformation());
        nextForm.contactInformation = copySection(oldForm.getContactInformation());
        nextForm.maritalStatusAndFamilyDetails = copySection(oldForm.getMaritalStatusAndFamilyDetails());
        nextForm.education = copySection(oldForm.getEducation());
        nextForm.employment = copySection(oldForm.getEmployment());
        nextForm.visitInformation = copySection(oldForm.getVisitInformation());
        nextForm.helpInformation = copySection(oldForm.getHelpInformation());
        nextForm.inviteInformation = copySection(oldForm.getInviteInformation());

        return nextForm;
    }

    public boolean requiresEnd() {
        return this.exportedAt != null && !this.exportedAt.toLocalDate().isAfter(LocalDate.now().minusDays(14)) && !this.isFinished;
    }
}

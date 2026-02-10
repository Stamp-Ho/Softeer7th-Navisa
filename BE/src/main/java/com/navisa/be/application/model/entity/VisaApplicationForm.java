package com.navisa.be.application.model.entity;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.common.model.entity.BaseEntity;
import com.navisa.be.common.model.entity.JobCode;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "visa_application_form")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VisaApplicationForm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "application_form_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private AgentProfile agentProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "foreigner_id", nullable = false)
    private ForeignerProfile foreignerProfile;

    @Column(name = "is_done", nullable = false)
    private Boolean isDone = false;

    @Column(name = "is_once_exported", nullable = false)
    private Boolean isOnceExported;

    @Column(name = "total_count")
    private Integer totalCount = 0;

    @Column(name = "current_step", nullable = false)
    private Integer currentStep = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_code_id", nullable = false)
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

    public VisaApplicationForm(AgentProfile agentProfile, ForeignerProfile foreignerProfile,
                               JobCode jobCode, Boolean isDone, Integer totalCount, Integer currentStep) {
        this.agentProfile = agentProfile;
        this.foreignerProfile = foreignerProfile;
        this.jobCode = jobCode;
        this.isDone = isDone;
        this.isOnceExported = false;
        this.totalCount = totalCount;
        this.currentStep = currentStep;
    }

    public void updateSections(List<Map<String, Object>> sections, Integer totalCount, Integer currentStep) {
        this.totalCount = totalCount;
        this.currentStep = currentStep;

        for (Map<String, Object> section : sections) {
            Number sectionIdNum = (Number) section.get("sectionId");
            if (sectionIdNum == null)
                continue;
            int sectionId = sectionIdNum.intValue();

            switch (sectionId) {
                case 1 -> this.personalDetail = section;
                case 2 -> this.passportInformation = section;
                case 3 -> this.contactInformation = section;
                case 4 -> this.maritalStatusAndFamilyDetails = section;
                case 5 -> this.education = section;
                case 6 -> this.employment = section;
                case 7 -> this.visitInformation = section;
                case 8 -> this.helpInformation = section;
                case 9 -> this.inviteInformation = section;
            }
        }
    }

    public void updateStatus(Boolean isDone) {
        this.isDone = isDone;
    }
}

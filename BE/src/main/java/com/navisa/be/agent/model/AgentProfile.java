package com.navisa.be.agent.model;

import com.navisa.be.common.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Table(name = "agent_profile")
@Entity
public class AgentProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "agent_id")
    private UUID id;

    @Column(name = "agent_name", nullable = false)
    private String name;

    @Column(name = "agent_birth", nullable = false)
    private LocalDate birthDate;

    @Column(name = "profile_img_url", nullable = false)
    private String profileImageUrl;

    @Column(name = "agent_business_time", nullable = false)
    private String businessTime;

    @Column(name = "office_name", nullable = false)
    private String officeName;

    @Column(name = "office_address", nullable = false)
    private String officeAddress;

    @Column(name = "detail_address", nullable = false)
    private String officeAddressDetail;

    @Column(name = "additional_history")
    private String additionalHistory;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @OneToMany(mappedBy = "agentProfile")
    private List<AgentLanguage> languages = new ArrayList<>();

    @OneToMany(mappedBy = "agentProfile")
    private List<AgentSpecializedJobCode> specializedJobCodes = new ArrayList<>();

    @Column(name = "license_no")
    private String licenseNo;

    @Column(name = "license_issued_at")
    private LocalDate licenseIssuedAt;

    @Column(name = "license_inner_page_no")
    private String licenseInnerPageNo;

    @Column(name = "license_management_no")
    private String licenseManagementNo;

    @Column(name = "agent_comment")
    private String comment;

    protected AgentProfile() {
    }

    public AgentProfile(String name,
                        LocalDate birthDate,
                        String profileImageUrl,
                        String businessTime,
                        String officeName,
                        String officeAddress,
                        String officeAddressDetail,
                        String additionalHistory,
                        UUID userId,
                        String licenseNo,
                        LocalDate licenseIssuedAt,
                        String licenseInnerPageNo,
                        String licenseManagementNo,
                        String comment) {
        this.name = name;
        this.birthDate = birthDate;
        this.profileImageUrl = profileImageUrl;
        this.businessTime = businessTime;
        this.officeName = officeName;
        this.officeAddress = officeAddress;
        this.officeAddressDetail = officeAddressDetail;
        this.additionalHistory = additionalHistory;
        this.userId = userId;
        this.licenseNo = licenseNo;
        this.licenseIssuedAt = licenseIssuedAt;
        this.licenseInnerPageNo = licenseInnerPageNo;
        this.licenseManagementNo = licenseManagementNo;
        this.comment = comment;
    }

    public void addSpecializedJobCodes(List<AgentSpecializedJobCode> codes) {
        this.specializedJobCodes.addAll(codes);
    }

    public void addLanguages(List<AgentLanguage> languages) {
        this.languages.addAll(languages);
    }
}

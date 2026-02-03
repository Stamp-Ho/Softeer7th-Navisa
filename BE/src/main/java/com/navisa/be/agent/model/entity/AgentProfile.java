package com.navisa.be.agent.model.entity;

import com.navisa.be.common.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "agent_profile", indexes = {
        // 1. 활성 점수순 정렬 + No-Offset 페이징 최적화
        @Index(name = "idx_agent_active_score_id", columnList = "active_score DESC, agent_id ASC"),

        // 2. 지역 검색 성능 향상 (단, LIKE '%keyword%'는 B-Tree의 한계가 있음)
        @Index(name = "idx_agent_office_address", columnList = "office_address")
})
@Getter
public class AgentProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "agent_id")
    private UUID id;

    @Column(name = "agent_name", nullable = false)
    private String name;

    @Column(name = "agent_birth", nullable = false)
    private LocalDate birthDate;

    @Column(name = "profile_object_key", nullable = false)
    private String profileObjectKey;

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
    private List<AgentSpecializedJob> specializedJobs = new ArrayList<>();

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

    @Column(name = "active_score", nullable = false)
    private double activeScore;

    protected AgentProfile() {
    }

    public AgentProfile(String name,
                        LocalDate birthDate,
                        String profileObjectKey,
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
                        String comment
        ) {
        this.name = name;
        this.birthDate = birthDate;
        this.profileObjectKey = profileObjectKey;
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
        this.activeScore = 100.0;
    }

    public void addSpecializedJobCodes(List<AgentSpecializedJob> codes) {
        this.specializedJobs.addAll(codes);
    }

    public void addLanguages(List<AgentLanguage> languages) {
        this.languages.addAll(languages);
    }
}

package com.navisa.be.foreigner.model.entity;

import com.navisa.be.foreigner.dto.ForeignerExpectedCompanyDto;

import com.navisa.be.common.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Table(name = "foreigner_expected_company")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ForeignerExpectedCompany extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expected_company_id")
    private Long id;

    @Column(name = "foreigner_id")
    private UUID foreignerId;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "start_date")
    private LocalDate startDate;

    public ForeignerExpectedCompanyDto toDto() {
        return new ForeignerExpectedCompanyDto(companyName, jobTitle, startDate);
    }

    public void update(String companyName, String jobTitle, LocalDate startDate) {
        this.companyName = companyName;
        this.jobTitle = jobTitle;
        this.startDate = startDate;
    }
}

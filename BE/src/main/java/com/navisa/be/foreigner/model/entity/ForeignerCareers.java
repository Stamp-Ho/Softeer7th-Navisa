package com.navisa.be.foreigner.model.entity;

import com.navisa.be.foreigner.dto.ForeignerCareerDto;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.navisa.be.common.model.entity.BaseEntity;

import java.time.LocalDate;
import java.util.UUID;

@Table(name = "foreigner_careers")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ForeignerCareers extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "career_id")
    private Long id;

    @Column(name = "foreigner_id")
    private UUID foreignerId;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_work")
    private boolean isWork;

    public ForeignerCareerDto toDto() {
        return new ForeignerCareerDto(companyName, jobTitle, startDate, endDate, isWork);
    }

    public void update(String jobTitle, LocalDate endDate, boolean isWork) {
        this.jobTitle = jobTitle;
        this.endDate = endDate;
        this.isWork = isWork;
    }
}

package com.navisa.be.foreigner.model.entity;

import com.navisa.be.foreigner.dto.ForeignerCareerDto;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.navisa.be.common.model.entity.BaseEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "foreigner_careers")
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

    public String getFormattedPeriod() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd.");

        if(this.startDate == null){
            return "";
        }

        String startStr = this.startDate.format(formatter);
        if (this.isWork || this.endDate == null) {
            return String.format("%s ~", startStr);
        }

        String endStr = this.endDate.format(formatter);
        return String.format("%s ~ %s", startStr, endStr);
    }

    public int getDurationMonths() {
        LocalDate endDate = this.endDate == null ? LocalDate.now() : this.endDate;

        if (this.startDate == null) {
            return 0;
        }

        // 시작일과 종료일 사이의 전체 개월 수 계산
        return (int) ChronoUnit.MONTHS.between(
                this.startDate.withDayOfMonth(1),
                endDate.withDayOfMonth(1).plusMonths(1)
        );
    }
}

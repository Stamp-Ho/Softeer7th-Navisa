package com.navisa.be.foreigner.model.entity;

import com.navisa.be.global.common.model.entity.BaseEntity;
import com.navisa.be.foreigner.model.enums.EducationDegreeLevel;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "foreigner_education")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ForeignerEducation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "foreigner_education_id")
    private Long id;

    @Column(name = "foreigner_id")
    private UUID foreignerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "degree_level")
    private EducationDegreeLevel degreeLevel;

    @Column(name = "school_name")
    private String schoolName;

    @Column(name = "major_name")
    private String majorName;

    public void update(EducationDegreeLevel degreeLevel, String schoolName, String majorName) {
        this.degreeLevel = degreeLevel;
        this.schoolName = schoolName;
        this.majorName = majorName;
    }
}

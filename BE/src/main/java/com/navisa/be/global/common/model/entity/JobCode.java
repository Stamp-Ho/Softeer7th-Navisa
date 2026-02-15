package com.navisa.be.global.common.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "job_code")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class JobCode extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_code_id")
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(name = "embedding_result", columnDefinition = "vector(512)")
    private float[] embeddingResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_group_id")
    private JobGroup jobGroup;
}

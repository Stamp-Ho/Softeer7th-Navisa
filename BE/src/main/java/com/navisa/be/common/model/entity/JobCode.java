package com.navisa.be.common.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Table(name = "job_code")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class JobCode {

    @Id
    @Column(name = "job_code_id")
    private Integer id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(name = "embedding_result", columnDefinition = "vector(512)")
    private List<Double> embeddingResult;
}

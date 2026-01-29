package com.navisa.be.foreigner.model.entity;

import com.navisa.be.common.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Table(name = "foreigner_similarity")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ForeignerSimilarity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "foreigner_similarity_id")
    private Long id;

    @Column(name = "foreigner_id")
    private UUID foreignerId;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "similarity_list", columnDefinition = "double precision[]")
    private double[] similarityList; // 초기화는 null 또는 빈 배열로 수행

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "job_code_id_list", columnDefinition = "bigint[]")
    private long[] jobCodeIdList;

}

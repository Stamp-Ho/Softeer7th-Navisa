package com.navisa.be.global.common.repository;

import com.navisa.be.global.common.dto.projection.JobCodeSimilarityProjection;
import com.navisa.be.global.common.model.entity.JobCode;
import com.navisa.be.agent.dto.projection.JobCodeProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobCodeRepository extends JpaRepository<JobCode, Long> {

    @Query(value = """
            SELECT
                job_code_id as id,
                name,
                (1 - (embedding_result <=> cast(:queryVector as vector))) as similarity
            FROM job_code
            ORDER BY similarity DESC
            LIMIT 3
            """, nativeQuery = true)
    List<JobCodeSimilarityProjection> findTop3SimilarJobCodes(@Param("queryVector") float[] queryVector);

    long countByIdIn(List<Long> ids);

    @Query(value = """
        SELECT new com.navisa.be.agent.dto.projection.JobCodeProjection(jc.id, jc.name, jc.code) 
        FROM JobCode jc 
        ORDER BY jc.id ASC
    """)
    List<JobCodeProjection> findAllJobCodeDtos();
}

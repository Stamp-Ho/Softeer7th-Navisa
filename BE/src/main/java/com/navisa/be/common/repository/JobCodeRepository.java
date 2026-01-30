package com.navisa.be.common.repository;

import com.navisa.be.common.dto.projection.JobCodeSimilarityProjection;
import com.navisa.be.common.model.entity.JobCode;
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
}

package com.navisa.be.application.repository;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.application.model.entity.VisaApplicationForm;
import com.navisa.be.application.repository.querydsl.ApplicationFormRepositoryQueryDsl;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationFormRepository extends JpaRepository<VisaApplicationForm, UUID>, ApplicationFormRepositoryQueryDsl {

    // 행정사가 담당하는 서류 중 최근 수정순으로 상위 6개 조회
    @EntityGraph(attributePaths = {"foreignerProfile"})
    List<VisaApplicationForm> findTop6ByAgentProfileOrderByUpdatedAtDesc(AgentProfile agentProfile);

    @Query(value = """
        SELECT * FROM visa_application_form v
        WHERE v.foreigner_id = :foreignerId 
          AND v.agent_id = :agentId
        ORDER BY v.created_at DESC 
        LIMIT 1
    """, nativeQuery = true)
    Optional<VisaApplicationForm> findCurrentAppFormNative(
            @Param("foreignerId") UUID foreignerId,
            @Param("agentId") UUID agentId
    );

    Optional<VisaApplicationForm> findFirstByForeignerProfileOrderByCreatedAtDesc(ForeignerProfile foreignerProfile);

    @EntityGraph(attributePaths = {"agentProfile"})
    Optional<VisaApplicationForm> findWithAgentProfileById(UUID id);

    // exportedAt의 날짜 부분이 targetDate와 일치하는 완료된 서류 조회
    @Query(value = """
        SELECT f FROM VisaApplicationForm f
        JOIN FETCH f.foreignerProfile fp
        JOIN FETCH f.agentProfile ap
        WHERE f.isDone = true
          AND f.exportedAt IS NOT NULL
          AND f.mailSentAt IS NULL
          AND CAST(f.exportedAt AS date) = :targetDate
    """)
    List<VisaApplicationForm> findAllByExportedDate(@Param("targetDate") LocalDate targetDate);

    // 특정 외국인의 비자 신청서 중 가장 최근 생성된 1건 조회
    Optional<VisaApplicationForm> findFirstByForeignerProfile_UserIdOrderByCreatedAtDesc(UUID userId);

    boolean existsByForeignerProfileIdAndAgentProfileIdAndIsFinishedTrue(UUID foreignerProfileId, UUID agentProfileId);

    Optional<VisaApplicationForm> findFirstByAgentProfileIdAndForeignerProfileIdOrderByCreatedAtDesc(
            UUID agentProfileId, UUID foreignerProfileId);
}

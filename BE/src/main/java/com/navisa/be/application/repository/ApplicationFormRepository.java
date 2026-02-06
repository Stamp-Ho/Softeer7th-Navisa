package com.navisa.be.application.repository;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.application.model.entity.VisaApplicationForm;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationFormRepository extends JpaRepository<VisaApplicationForm, UUID> {

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
}

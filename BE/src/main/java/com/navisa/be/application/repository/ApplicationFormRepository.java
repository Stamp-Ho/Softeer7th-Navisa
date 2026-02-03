package com.navisa.be.application.repository;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.application.model.entity.VisaApplicationForm;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ApplicationFormRepository extends JpaRepository<VisaApplicationForm, UUID> {

    // 행정사가 담당하는 서류 중 최근 수정순으로 상위 6개 조회
    @EntityGraph(attributePaths = {"foreignerProfile"})
    List<VisaApplicationForm> findTop6ByAgentProfileOrderByUpdatedAtDesc(AgentProfile agentProfile);
}

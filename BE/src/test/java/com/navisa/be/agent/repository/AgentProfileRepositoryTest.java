package com.navisa.be.agent.repository;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.model.entity.AgentSpecializedJobCode;
import com.navisa.be.common.model.entity.JobCode;
import com.navisa.be.common.repository.JobCodeRepository;
import com.navisa.be.support.IntegrationTestSupport;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class AgentProfileRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private AgentProfileRepository agentProfileRepository;

    @Autowired
    private AgentSpecializedJobCodeRepository specializedJobCodeRepository;

    @Autowired
    private JobCodeRepository jobCodeRepository; // JobCode도 저장소 필요

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("각 Repository를 사용한 연관관계 저장 및 조회 테스트")
    void testRepositoryMapping() {
        // given
        float[] vector = new float[512];
        JobCode jobCode = new JobCode(null, "DEV_01", "백엔드 개발", vector);
        jobCodeRepository.save(jobCode);

        AgentProfile agent = new AgentProfile(
                "강감찬",
                LocalDate.of(1985, 5, 20),
                "profile.jpg",
                "10:00~19:00",
                "행운부동산",
                "서울시 강남구",
                "2층",
                "공인중개사 10년차",
                UUID.randomUUID(),
                "LIC-999",
                LocalDate.now(),
                "page-1",
                "m-no-1",
                "전문가입니다."
        );
        agentProfileRepository.save(agent);

        // when
        AgentSpecializedJobCode mapping = new AgentSpecializedJobCode(agent, jobCode);
        specializedJobCodeRepository.save(mapping);

        entityManager.flush();
        entityManager.clear();

        // then
        // 새로 select 해서 연관관계가 잘 잡히는지 확인
        AgentProfile savedAgent = agentProfileRepository.findById(agent.getId()).orElseThrow();

        assertThat(savedAgent.getSpecializedJobCodes()).hasSize(1);
        assertThat(savedAgent.getSpecializedJobCodes().get(0).getJobCode().getName())
                .isEqualTo("백엔드 개발");
    }
}
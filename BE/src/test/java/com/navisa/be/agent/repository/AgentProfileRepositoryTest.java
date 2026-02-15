package com.navisa.be.agent.repository;

import com.navisa.be.agent.dto.AgentCardQueryDto;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.support.AgentProfileTestFixture;
import com.navisa.be.global.web.request.SliceRequest;
import com.navisa.be.global.common.model.entity.JobCode;
import com.navisa.be.global.common.model.entity.Language;
import com.navisa.be.support.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class AgentProfileRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private AgentProfileRepository agentProfileRepository;

    @Autowired
    private AgentProfileTestFixture agentProfileTestFixture;

    private JobCode jobCode1;
    private Language language1;

    @BeforeEach
    void setUp() {
        String uniqueJobCode = "JC-" + UUID.randomUUID().toString().substring(0, 8);
        jobCode1 = agentProfileTestFixture.createJobCode(uniqueJobCode, "Job Name " + uniqueJobCode);

        String uniqueLang = "Lang-" + UUID.randomUUID().toString().substring(0, 8);
        language1 = agentProfileTestFixture.createLanguage(uniqueLang);

        assertThat(jobCode1.getId()).isNotNull();
        assertThat(language1.getId()).isNotNull();
    }

    @Test
    @DisplayName("필터 검색: 직종, 언어, 지역 조건에 맞는 행정사를 조회한다 (activeScore 내림차순 정렬)")
    void findByFilters_shouldReturnMatchingProfiles() {
        // given
        AgentProfile pA = agentProfileTestFixture.createAgentProfile("Agent A", "서울시 강남구", jobCode1, language1);
        org.springframework.test.util.ReflectionTestUtils.setField(pA, "activeScore", 200.0);
        agentProfileRepository.save(pA);

        AgentProfile pB = agentProfileTestFixture.createAgentProfile("Agent B", "서울시 강남구", jobCode1, language1);
        org.springframework.test.util.ReflectionTestUtils.setField(pB, "activeScore", 100.0);
        agentProfileRepository.save(pB);

        agentProfileTestFixture.createAgentProfile("Agent C", "부산시 해운대구", jobCode1, language1);

        AgentCardQueryDto request = new AgentCardQueryDto(
                List.of(jobCode1.getId()), // jobIdList
                List.of("서울"), // regionList
                List.of(language1.getId()) // languageIdList
        );
        SliceRequest<UUID> sliceRequest = new SliceRequest<>(null, 10);

        // when
        List<AgentProfile> result = agentProfileRepository.findByFilters(request, sliceRequest);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Agent A"); // Score 200
        assertThat(result.get(1).getName()).isEqualTo("Agent B"); // Score 100
    }

    @Test
    @DisplayName("필터 검색: 커서 페이징 동작 확인 (마지막 요소의 ActiveScore 기준 이후 데이터 조회)")
    void findByFilters_shouldWorkWithCursor() {
        // given
        AgentProfile pA = agentProfileTestFixture.createAgentProfile("Agent A", "서울시 강남구", jobCode1, language1);
        org.springframework.test.util.ReflectionTestUtils.setField(pA, "activeScore", 300.0);
        agentProfileRepository.save(pA);

        AgentProfile pB = agentProfileTestFixture.createAgentProfile("Agent B", "서울시 강남구", jobCode1, language1);
        org.springframework.test.util.ReflectionTestUtils.setField(pB, "activeScore", 200.0);
        agentProfileRepository.save(pB);

        AgentProfile pC = agentProfileTestFixture.createAgentProfile("Agent C", "서울시 강남구", jobCode1, language1);
        org.springframework.test.util.ReflectionTestUtils.setField(pC, "activeScore", 100.0);
        agentProfileRepository.save(pC);

        AgentCardQueryDto request = new AgentCardQueryDto(
                List.of(jobCode1.getId()),
                List.of("서울"),
                List.of(language1.getId()));

        SliceRequest<UUID> sliceRequest1 = new SliceRequest<>(null, 2);
        List<AgentProfile> result1 = agentProfileRepository.findByFilters(request, sliceRequest1);
        assertThat(result1).hasSize(3);
        assertThat(result1.get(0).getName()).isEqualTo("Agent A");
        assertThat(result1.get(1).getName()).isEqualTo("Agent B");

        SliceRequest<UUID> sliceRequest2 = new SliceRequest<>(pB.getId(), 2);
        List<AgentProfile> result2 = agentProfileRepository.findByFilters(request, sliceRequest2);

        // then
        assertThat(result2).hasSize(1);
        assertThat(result2.get(0).getName()).isEqualTo("Agent C");
    }

    @Test
    @DisplayName("필터 검색: 지역 검색어 확장 (경북 -> 경상북도 포함) 확인")
    void findByFilters_shouldExpandRegionKeyword() {
        // given
        AgentProfile profile1 = agentProfileTestFixture.createAgentProfile("Agent Gyeongbuk", "경상북도 구미시", jobCode1,
                language1);

        AgentCardQueryDto request = new AgentCardQueryDto(
                List.of(jobCode1.getId()),
                List.of("경북"),
                List.of(language1.getId()));
        SliceRequest<UUID> sliceRequest = new SliceRequest<>(null, 10);

        // when
        List<AgentProfile> result = agentProfileRepository.findByFilters(request, sliceRequest);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(profile1.getId());
    }

    @Test
    @DisplayName("필터 검색: 필터 값이 모두 null일 때 전체 데이터 조회 확인 (ActiveScore 내림차순)")
    void findByFilters_shouldReturnAllWhenFiltersAreNull() {
        // given
        AgentProfile pA = agentProfileTestFixture.createAgentProfile("Agent A", "서울시", jobCode1, language1);
        org.springframework.test.util.ReflectionTestUtils.setField(pA, "activeScore", 200.0);
        agentProfileRepository.save(pA);

        AgentProfile pB = agentProfileTestFixture.createAgentProfile("Agent B", "부산시", jobCode1, language1);
        org.springframework.test.util.ReflectionTestUtils.setField(pB, "activeScore", 100.0);
        agentProfileRepository.save(pB);

        AgentCardQueryDto request = new AgentCardQueryDto(null, null, null);
        SliceRequest<UUID> sliceRequest = new SliceRequest<>(null, 10);

        // when
        List<AgentProfile> result = agentProfileRepository.findByFilters(request, sliceRequest);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.stream().map(AgentProfile::getName)).containsExactly("Agent A", "Agent B");
    }

}
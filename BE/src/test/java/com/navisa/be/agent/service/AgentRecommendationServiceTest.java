package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.response.AgentCardResponse;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.model.entity.ForeignerSimilarity;
import com.navisa.be.foreigner.service.ForeignerProfileCrudService;
import com.navisa.be.global.common.service.StorageService;
import com.navisa.be.recommendation.calculator.FinalRecommendationCalculator;
import com.navisa.be.recommendation.calculator.ReviewBonusCalculator;
import com.navisa.be.recommendation.calculator.SpecialtyDistributionCalculator;
import com.navisa.be.support.AgentFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgentRecommendationServiceTest {

    @InjectMocks
    private AgentRecommendationService agentRecommendationService;

    @Mock
    private ForeignerProfileCrudService foreignerProfileCrudService;

    @Mock
    private AgentProfileRepository agentProfileRepository;

    @Mock
    private RedisTemplate<String, Double> doubleRedisTemplate;

    @Mock
    private ValueOperations<String, Double> valueOperations;

    @Mock
    private SpecialtyDistributionCalculator distributionCalculator;

    @Mock
    private ReviewBonusCalculator reviewBonusCalculator;

    @Mock
    private FinalRecommendationCalculator finalCalculator;

    @Mock
    private StorageService storageService;

    @Mock
    private AgentSpecializedJobService agentSpecializedJobService;

    @Mock
    private AgentBadgeService agentBadgeService;

    @Test
    @DisplayName("Redis MGET을 통한 Batch 조회를 활용하여 추천 순위가 정렬된다")
    void getPersonalizedAgents_SortingWithBatchRedisTest() {
        // given
        String email = "test@example.com";

        UUID highId = UUID.randomUUID();
        UUID lowId = UUID.randomUUID();

        AgentProfile highAgent = AgentFixture.createAgentProfile(highId, 100.0);
        AgentProfile lowAgent = AgentFixture.createAgentProfile(lowId, 50.0);

        setupCommonMocks(email, List.of(lowAgent, highAgent));
        given(valueOperations.multiGet(anyList())).willReturn(List.of(0.0, 0.0));

        given(finalCalculator.calculateFinalGradeByLongId(anyMap(), anyMap()))
                .willReturn(10.0, 100.0);

        // when
        List<AgentCardResponse> result = agentRecommendationService.getPersonalizedAgents(email);

        // then
        assertThat(result.get(0).agentId())
                .as("추천 점수가 가장 높은 행정사(ID: %s)가 첫 번째로 정렬되어야 함", highId)
                .isEqualTo(highId);

        assertThat(result.get(0).agentId()).isEqualTo(highId);
    }

    @Test
    @DisplayName("MGET을 사용하여 네트워크 라운드트립을 1회로 제한한다")
    void verifyMgetConsolidatesNetworkRequests() {
        // given
        String email = "test@email.com";

        List<AgentProfile> profiles = IntStream.range(0, 100)
                .mapToObj(i -> AgentFixture.createAgentProfile(UUID.randomUUID(), 50.0 + i))
                .toList();

        setupCommonMocks(email, profiles);
        given(valueOperations.multiGet(anyList())).willReturn(Collections.nCopies(100, 0.0));
        given(finalCalculator.calculateFinalGradeByLongId(anyMap(), anyMap())).willReturn(1.0);

        // when
        agentRecommendationService.getPersonalizedAgents(email);

        // then
        verify(valueOperations, times(1)).multiGet(argThat(list -> ((List<?>) list).size() == 100));
        verify(valueOperations, never()).get(anyString());
    }

    @Test
    @DisplayName("상위 12개 행정사에 대해 specializedJob과 badge를 배치로 1회씩만 조회한다")
    void getPersonalizedAgents_BatchQueriesCalledOnce() {
        // given
        String email = "batch@test.com";

        List<AgentProfile> profiles = IntStream.range(0, 20)
                .mapToObj(i -> AgentFixture.createAgentProfile(UUID.randomUUID(), 50.0 + i))
                .toList();

        setupCommonMocks(email, profiles);
        given(valueOperations.multiGet(anyList())).willReturn(Collections.nCopies(20, 0.0));
        given(finalCalculator.calculateFinalGradeByLongId(anyMap(), anyMap())).willReturn(1.0);

        // when
        agentRecommendationService.getPersonalizedAgents(email);

        // then
        verify(agentSpecializedJobService, times(1))
                .getTop2SpecializedJobIdsBatch(argThat(ids -> ids.size() == 12));
        verify(agentBadgeService, times(1))
                .getTop2BadgeIdsBatch(argThat(ids -> ids.size() == 12));

        verify(agentSpecializedJobService, never()).getTop2SpecializedJobIds(any());
        verify(agentBadgeService, never()).getTop2BadgeIds(any());
    }

    private void setupCommonMocks(String email, List<AgentProfile> profiles) {
        ForeignerProfile mockForeignerProfile = Mockito.mock(ForeignerProfile.class);
        UUID foreignerId = UUID.randomUUID();
        given(foreignerProfileCrudService.findByEmail(email)).willReturn(mockForeignerProfile);
        given(mockForeignerProfile.getId()).willReturn(foreignerId);

        ForeignerSimilarity similarity = Mockito.mock(ForeignerSimilarity.class);
        given(similarity.getJobCodeIdList()).willReturn(new long[]{1L});
        given(similarity.getSimilarityList()).willReturn(new double[]{1.0});
        given(foreignerProfileCrudService.findSimilarityByForeignerId(foreignerId)).willReturn(similarity);

        given(agentProfileRepository.findAllValidAgentProfiles()).willReturn(profiles);
        given(doubleRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(distributionCalculator.calculateDistribution(anyInt())).willReturn(1.0);
        given(reviewBonusCalculator.calculateBonusFactor(anyDouble())).willReturn(1.0);
        given(reviewBonusCalculator.calculateFinalDistribution(anyDouble(), anyDouble())).willReturn(1.0);
        given(storageService.getImgUrl(any(), any(), anyBoolean())).willReturn("url");

        // 배치 조회 mock: 각 agentId에 대해 빈 리스트 반환
        given(agentSpecializedJobService.getTop2SpecializedJobIdsBatch(anyList()))
                .willAnswer(inv -> {
                    List<UUID> ids = inv.getArgument(0);
                    Map<UUID, List<Long>> result = new java.util.HashMap<>();
                    ids.forEach(id -> result.put(id, List.of()));
                    return result;
                });
        given(agentBadgeService.getTop2BadgeIdsBatch(anyList()))
                .willAnswer(inv -> {
                    List<UUID> ids = inv.getArgument(0);
                    Map<UUID, List<Long>> result = new java.util.HashMap<>();
                    ids.forEach(id -> result.put(id, List.of()));
                    return result;
                });
    }
}

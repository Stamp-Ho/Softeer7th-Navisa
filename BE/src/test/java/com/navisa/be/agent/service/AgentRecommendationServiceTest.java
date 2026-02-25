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

import java.util.*;

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

        // Fixture 생성
        AgentProfile highAgent = AgentFixture.createAgentProfile(highId, 100.0);
        AgentProfile lowAgent = AgentFixture.createAgentProfile(lowId, 50.0);

        setupCommonMocks(email, List.of(lowAgent, highAgent));

        // [수정] 호출 순서에 의존하지 않고, 인자의 내용에 따라 점수를 반환하도록 Answer 설정
        given(finalCalculator.calculateFinalGradeByLongId(anyMap(), anyMap()))
                .willAnswer(invocation -> {
                    Map<Long, Double> specialtyScores = invocation.getArgument(0);
                    // highAgent의 activeScore(100.0)가 반영된 맵인지 확인하여 점수 차등 부여
                    // (실제 로직에서 activeScore가 saMap에 담기므로 이를 활용)
                    if (specialtyScores.values().stream().anyMatch(v -> v >= 100.0)) {
                        return 100.0;
                    }
                    return 10.0;
                });

        // when
        List<AgentCardResponse> result = agentRecommendationService.getPersonalizedAgents(email);

        // then
        assertThat(result.get(0).agentId())
                .as("추천 점수가 100.0으로 계산된 행정사가 첫 번째여야 함")
                .isEqualTo(highId);
    }

    @Test
    @DisplayName("가중치 조회 시 MGET을 활용하여 네트워크 라운드트립을 1회로 제한한다")
    void verifyMgetConsolidatesNetworkRequests() {
        // given
        String email = "test@email.com";
        long[] targetJobs = new long[]{101L, 102L};
        List<Long> targetJobList = List.of(101L, 102L);

        List<AgentProfile> profiles = IntStream.range(0, 100)
                .mapToObj(i -> AgentFixture.createAgentProfile(UUID.randomUUID(), 50.0 + i))
                .toList();

        setupCommonMocksWithJobs(email, profiles, targetJobs);

        given(agentSpecializedJobService.getCombinedWeightMap(anyList(), anyList()))
                .willReturn(new HashMap<>());

        // when
        agentRecommendationService.getPersonalizedAgents(email);

        // then
        verify(agentSpecializedJobService, times(1))
                .getCombinedWeightMap(
                        argThat(idList -> idList.size() == 100), // 100명분 ID 일괄 전달 여부
                        argThat(jobIds -> jobIds.size() == 2 && jobIds.containsAll(targetJobList)) // 타겟 직종 포함 여부
                );
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

    private void setupCommonMocksWithJobs(String email, List<AgentProfile> profiles, long[] targetJobs) {
        // 1. 외국인 프로필 및 ID 모킹
        ForeignerProfile mockForeignerProfile = Mockito.mock(ForeignerProfile.class);
        UUID foreignerId = UUID.randomUUID();
        given(foreignerProfileCrudService.findByEmail(email)).willReturn(mockForeignerProfile);
        given(mockForeignerProfile.getId()).willReturn(foreignerId);

        // 2. 외국인 관심 직종(targetJobs) 및 유사도 리스트 설정
        ForeignerSimilarity similarity = Mockito.mock(ForeignerSimilarity.class);
        given(similarity.getJobCodeIdList()).willReturn(targetJobs);

        // 유사도 리스트(Weights)는 jobIds와 동일한 길이로 설정 (모두 1.0으로 가중치 부여)
        double[] weights = new double[targetJobs.length];
        Arrays.fill(weights, 1.0);
        given(similarity.getSimilarityList()).willReturn(weights);

        given(foreignerProfileCrudService.findSimilarityByForeignerId(foreignerId)).willReturn(similarity);

        // 3. 행정사 프로필 전수 조회 결과 설정
        given(agentProfileRepository.findAllValidAgentProfiles()).willReturn(profiles);

        // 4. 계산기 및 공통 서비스 모킹 (기본값 1.0 반환)
        given(distributionCalculator.calculateDistribution(anyInt())).willReturn(1.0);
        given(reviewBonusCalculator.calculateBonusFactor(anyDouble())).willReturn(1.0);
        given(reviewBonusCalculator.calculateFinalDistribution(anyDouble(), anyDouble())).willReturn(1.0);
        given(storageService.getImgUrl(any(), any(), anyBoolean())).willReturn("url");

        // Top2 전문 분야 및 배지 배치 조회 모킹 (ID 리스트를 받아 빈 맵 생성)
        given(agentSpecializedJobService.getTop2SpecializedJobIdsBatch(anyList()))
                .willAnswer(inv -> {
                    List<UUID> ids = inv.getArgument(0);
                    Map<UUID, List<Long>> result = new HashMap<>();
                    ids.forEach(id -> result.put(id, List.of()));
                    return result;
                });

        given(agentBadgeService.getTop2BadgeIdsBatch(anyList()))
                .willAnswer(inv -> {
                    List<UUID> ids = inv.getArgument(0);
                    Map<UUID, List<Long>> result = new HashMap<>();
                    ids.forEach(id -> result.put(id, List.of()));
                    return result;
                });
    }
}

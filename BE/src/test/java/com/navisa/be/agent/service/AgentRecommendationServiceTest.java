package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.projection.AgentSimpleProjection;
import com.navisa.be.agent.dto.response.AgentCardResponse;
import com.navisa.be.agent.model.entity.AgentSpecializedJobSummary;
import com.navisa.be.agent.model.enums.OfficeAddressRegion;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.agent.repository.AgentSpecializedJobSummaryRepository;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.service.ForeignerProfileCrudService;
import com.navisa.be.global.common.model.entity.JobCode;
import com.navisa.be.foreigner.model.entity.ForeignerSimilarity;
import com.navisa.be.global.common.service.StorageService;
import com.navisa.be.recommendation.calculator.FinalRecommendationCalculator;
import com.navisa.be.recommendation.calculator.ReviewBonusCalculator;
import com.navisa.be.recommendation.calculator.SpecialtyDistributionCalculator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

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
    private AgentSpecializedJobSummaryRepository summaryRepository;

    @Mock
    private StorageService storageService;

    @Mock
    private AgentSpecializedJobService agentSpecializedJobService;

    @Mock
    private AgentBadgeService agentBadgeService;

    @Test
    @DisplayName("사용자의 관심도와 행정사의 전문 분야 점수를 합산하여 추천 순위대로 정렬된다")
    void getPersonalizedAgents_SortingTest() {
        // given
        String email = "email";
        UUID foreignerId = UUID.randomUUID();
        ForeignerProfile mockForeignerProfile = Mockito.mock(ForeignerProfile.class);
        given(foreignerProfileCrudService.findByEmail(any())).willReturn(mockForeignerProfile);
        given(mockForeignerProfile.getId()).willReturn(foreignerId);

        ForeignerSimilarity similarity = Mockito.mock(ForeignerSimilarity.class);
        given(similarity.getJobCodeIdList()).willReturn(new long[] { 1L });
        given(similarity.getSimilarityList()).willReturn(new double[] { 1.0 });
        UUID highId = UUID.randomUUID();
        UUID lowId = UUID.randomUUID();

        given(agentSpecializedJobService.getTop2SpecializedJobIdsBatch(any()))
                .willReturn(Map.of(highId, List.of(1L, 2L), lowId, List.of(1L, 2L)));
        given(agentBadgeService.getTop2BadgeIdsBatch(any()))
                .willReturn(Map.of(highId, List.of(1L, 2L), lowId, List.of(1L, 2L)));

        AgentSimpleProjection highAgent = new AgentSimpleProjection(
                highId,
                "High Agent",
                "high-profile-key",
                OfficeAddressRegion.SEOUL.getAliases().get(0),
                5L,
                100.0);
        AgentSimpleProjection lowAgent = new AgentSimpleProjection(
                lowId,
                "Low Agent",
                "low-profile-key",
                OfficeAddressRegion.BUSAN.getAliases().get(0),
                3L,
                50.0);
        JobCode highJobCode = Mockito.mock(JobCode.class);
        JobCode lowJobCode = Mockito.mock(JobCode.class);
        given(highJobCode.getId()).willReturn(1L);
        given(lowJobCode.getId()).willReturn(2L);

        given(agentProfileRepository.findAllValidAgentProjections()).willReturn(List.of(lowAgent, highAgent));
        given(foreignerProfileCrudService.findSimilarityByForeignerId(foreignerId)).willReturn(similarity);

        given(finalCalculator.calculateFinalGradeByLongId(any(), any())).willAnswer(invocation -> {
            Map<Long, Double> saMap = invocation.getArgument(0);
            double sampleValue = saMap.values().stream().findFirst().orElse(0.0);
            return sampleValue > 70.0 ? 1000.0 : 10.0;
        });

        given(summaryRepository.findAllByAgentIdIn(any())).willReturn(List.of(
                new AgentSpecializedJobSummary(highId, highJobCode),
                new AgentSpecializedJobSummary(lowId, lowJobCode)));

        given(distributionCalculator.calculateDistribution(any(Integer.class))).willReturn(1.0);
        given(reviewBonusCalculator.calculateBonusFactor(any(Double.class))).willReturn(1.0);
        given(reviewBonusCalculator.calculateFinalDistribution(any(Double.class), any(Double.class))).willReturn(1.0);
        given(storageService.getImgUrl(any(), any(), any(Boolean.class))).willReturn("url");

        // when
        List<AgentCardResponse> result = agentRecommendationService.getPersonalizedAgents(email);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).agentId()).isEqualTo(highId);
        assertThat(result.get(1).agentId()).isEqualTo(lowId);
    }
}

package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.response.AgentCardResponse;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.model.entity.AgentSpecializedJobSummary;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.agent.repository.AgentSpecializedJobSummaryRepository;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.service.ForeignerProfileCrudService;
import com.navisa.be.global.common.model.entity.JobCode;
import com.navisa.be.foreigner.model.entity.ForeignerSimilarity;
import com.navisa.be.foreigner.repository.ForeignerSimilarityRepository;
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
    private ForeignerSimilarityRepository foreignerSimilarityRepository;

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
        given(agentSpecializedJobService.getTop2SpecializedJobIds(any())).willReturn(List.of(1L, 2L));
        given(agentBadgeService.getTop2BadgeIds(any())).willReturn(List.of(1L, 2L));

        UUID highId = UUID.randomUUID();
        UUID lowId = UUID.randomUUID();
        AgentProfile highAgent = Mockito.mock(AgentProfile.class);
        AgentProfile lowAgent = Mockito.mock(AgentProfile.class);
        JobCode highJobCode = Mockito.mock(JobCode.class);
        JobCode lowJobCode = Mockito.mock(JobCode.class);
        given(highJobCode.getId()).willReturn(1L);
        given(lowJobCode.getId()).willReturn(1L);

        given(highAgent.getId()).willReturn(highId);
        given(lowAgent.getId()).willReturn(lowId);
        given(highAgent.getActiveScore()).willReturn(100.0);
        given(lowAgent.getActiveScore()).willReturn(50.0);
        given(highAgent.getSpecializedJobs()).willReturn(List.of());
        given(lowAgent.getSpecializedJobs()).willReturn(List.of());
        given(highAgent.getProfileObjectKey()).willReturn("high-profile-key");
        given(lowAgent.getProfileObjectKey()).willReturn("low-profile-key");

        given(agentProfileRepository.findAllValidAgentProfiles()).willReturn(List.of(lowAgent, highAgent));
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

package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.response.ReviewReliabilityResponse;
import com.navisa.be.agent.exception.AgentException;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.model.entity.ForeignerSimilarity;
import com.navisa.be.foreigner.repository.ForeignerSimilarityRepository;
import com.navisa.be.foreigner.service.ForeignerQueryService;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.recommendation.calculator.ReviewReliabilityCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReviewReliabilityService {

    private final ForeignerQueryService foreignerQueryService;
    private final ReviewReliabilityCalculator reliabilityCalculator;

    @Transactional(readOnly = true)
    public ReviewReliabilityResponse getReviewReliability(ForeignerProfile foreignerProfile) {
        // 행정사 특화 분야 추천을 위한 상대 신뢰도(ria) 계산
        ForeignerSimilarity similarity = foreignerQueryService.findSimilarityByForeignerId(foreignerProfile.getId());

        // 상위 3개 데이터 추출
        List<Long> allJobCodeIds = Arrays.stream(similarity.getJobCodeIdList()).boxed().toList();
        List<Double> allSimilarities = Arrays.stream(similarity.getSimilarityList()).boxed().toList();

        int topLimit = Math.min(3, allSimilarities.size());
        List<Long> top3JobIds = allJobCodeIds.subList(0, topLimit);

        // r_i,a = w_i,a / Σ(w_i,k) 계산
        List<Double> top3Similarities = allSimilarities.subList(0, topLimit);
        List<Double> relativeRatios = top3Similarities.stream()
                .map(targetW -> reliabilityCalculator.calculateRelativeRatio(targetW, top3Similarities))
                .collect(Collectors.toList());

        return new ReviewReliabilityResponse(top3JobIds, relativeRatios);
    }
}

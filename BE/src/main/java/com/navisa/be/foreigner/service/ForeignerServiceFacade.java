package com.navisa.be.foreigner.service;

import com.navisa.be.common.dto.projection.JobCodeSimilarityProjection;
import com.navisa.be.common.exception.BaseException;
import com.navisa.be.common.infrastructure.client.GeminiEmbeddingRequestType;
import com.navisa.be.common.infrastructure.client.GeminiTextEmbeddingClient;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.common.repository.JobCodeRepository;
import com.navisa.be.foreigner.dto.request.ForeignerRegisterRequest;
import com.navisa.be.foreigner.dto.response.ForeignerQueryResponse;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.user.service.UserQueryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class ForeignerServiceFacade {

    private final ForeignerCommandService foreignerCommandService;
    private final GeminiTextEmbeddingClient geminiTextEmbeddingClient;
    private final JobCodeRepository jobCodeRepository;
    private final UserQueryService userQueryService;
    private final ForeignerQueryService foreignerQueryService;

    @Transactional
    public void registerAllForeignerInfo(ForeignerRegisterRequest request, String email) {
        UUID userId = userQueryService.findByEmail(email).getId();

        // 1. Foreigner에 대해 유사도를 제외한 모든 정보를 DB에 저장
        ForeignerProfile profile = foreignerCommandService.registerForeignerTotalInfo(request, userId);

        // 2. 사용자가 입력한 자연어 직무에 대해서 text_embedding 결과 조회하기
        float[] embedding_result = geminiTextEmbeddingClient.embedText(
                request.expectedCompany().jobTitle(), GeminiEmbeddingRequestType.QUERY);

        // 3. 유사도 비교했을 떄의 상위 3개를 반환
        List<JobCodeSimilarityProjection> projectionList = jobCodeRepository.findTop3SimilarJobCodes(embedding_result);

        // 4. 계산된 유사도를 저장
        if (projectionList.size() != 3)
            throw new BaseException(ResponseStatus.SIMILARITY_CALCULATE_FAIL);

        foreignerCommandService.registerCalculatedSimilarity(profile, projectionList);
    }

    public ForeignerQueryResponse findForeignerTotalInfo(String email) {
        UUID userId = userQueryService.findByEmail(email).getId();
        return foreignerQueryService.findForeignerTotalInfo(userId);
    }
}

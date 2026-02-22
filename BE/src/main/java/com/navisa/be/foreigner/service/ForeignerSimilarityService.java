package com.navisa.be.foreigner.service;

import com.navisa.be.foreigner.exception.ForeignerException;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.global.common.dto.projection.JobCodeSimilarityProjection;
import com.navisa.be.global.common.service.JobCodeService;
import com.navisa.be.global.web.response.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ForeignerSimilarityService {

    private final JobCodeService jobCodeService;
    private final ForeignerProfileCrudService foreignerProfileCrudService;

    @Transactional
    public void processSimilarity(UUID foreignerId, float[] embedding) {
        List<JobCodeSimilarityProjection> top3 = jobCodeService.findTop3SimilarJobCodes(embedding);
        if (top3.size() != 3) {
            throw new ForeignerException(ResponseStatus.SIMILARITY_CALCULATE_FAIL);
        }
        ForeignerProfile profile = foreignerProfileCrudService.findById(foreignerId);
        foreignerProfileCrudService.registerCalculatedSimilarity(profile, top3);
    }
}
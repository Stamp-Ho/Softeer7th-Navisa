package com.navisa.be.foreigner.service;

import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.model.entity.ForeignerSimilarity;
import com.navisa.be.foreigner.repository.ForeignerSimilarityRepository;
import com.navisa.be.global.common.model.entity.JobCode;
import com.navisa.be.global.common.repository.JobCodeRepository;
import com.navisa.be.support.ForeignerProfileTestFixture;
import com.navisa.be.support.IntegrationTestSupport;
import com.navisa.be.support.UserTestFixture;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ForeignerSimilarityServiceTest extends IntegrationTestSupport {

    @Autowired
    private ForeignerSimilarityService foreignerSimilarityService;

    @Autowired
    private ForeignerSimilarityRepository foreignerSimilarityRepository;

    @Autowired
    private JobCodeRepository jobCodeRepository;

    @Autowired
    private UserTestFixture userTestFixture;

    @Autowired
    private ForeignerProfileTestFixture foreignerProfileTestFixture;

    @Test
    @DisplayName("외국인 유사도 저장에 성공한다")
    void processSimilarity_success() {
        // given
        User user = userTestFixture.createUser("test@example.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile profile = foreignerProfileTestFixture.createForeignerProfile(user);

        float[] vector1 = createVector(512, 0.9f);
        float[] vector2 = createVector(512, 0.5f);
        float[] vector3 = createVector(512, 0.1f);

        JobCode job1 = new JobCode(null, "CODE1", "Job 1", vector1, null);
        JobCode job2 = new JobCode(null, "CODE2", "Job 2", vector2, null);
        JobCode job3 = new JobCode(null, "CODE3", "Job 3", vector3, null);
        jobCodeRepository.saveAll(List.of(job1, job2, job3));

        float[] queryVector = createVector(512, 0.95f);

        // when
        foreignerSimilarityService.processSimilarity(profile.getId(), queryVector);

        // then
        Optional<ForeignerSimilarity> result = foreignerSimilarityRepository.findByForeignerId(profile.getId());
        assertThat(result).isPresent();

        ForeignerSimilarity similarity = result.get();
        assertThat(similarity.getJobCodeIdList()).hasSize(3);
        assertThat(similarity.getJobCodeIdList()).contains(job1.getId());
    }

    private float[] createVector(int dim, float value) {
        float[] vector = new float[dim];
        for (int i = 0; i < dim; i++) {
            vector[i] = value;
        }
        return vector;
    }
}

package com.navisa.be.global.common.repository;

import com.navisa.be.global.common.dto.projection.JobCodeSimilarityProjection;
import com.navisa.be.global.common.model.entity.JobCode;
import com.navisa.be.global.common.repository.JobCodeRepository;
import com.navisa.be.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

@Transactional
class JobCodeRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private JobCodeRepository jobCodeRepository;

    @Test
    @DisplayName("Floats[]로 쿼리하고 Double로 반환받는 Top3 유사도 검색이 정상 작동한다")
    void findTop3SimilarJobCodes() {
        // given
        // 512차원 벡터 생성 (스키마 제약조건)

        // Query Vector: [1, 0, 0, ...] - 1번째 차원 강조
        float[] queryVector = createVector(v -> v[0] = 1.0f);

        // Job A: [0.9, 0, 0, ...] - 1번째 차원 강조 (방향 일치, 유사도 1.0)
        saveJobCode("JOB001", "Software Engineer", v -> v[0] = 0.9f);

        // Job B: [0.7, 0.7, 0, ...] - 1,2번째 차원 반반 (약간 다름, 45도, 유사도 ~0.707)
        saveJobCode("JOB002", "Data Scientist", v -> {
            v[0] = 0.7f;
            v[1] = 0.7f;
        });

        // Job C: [0, 1.0, 0, ...] - 2번째 차원 강조 (직교, 유사도 0.0)
        saveJobCode("JOB003", "Project Manager", v -> v[1] = 1.0f);

        // Job D: [0, 0, 1.0, ...] - 3번째 차원 강조 (직교, 유사도 0.0)
        saveJobCode("JOB004", "Chef", v -> v[2] = 1.0f);

        // Job E: [-1.0, 0, 0, ...] - 1번째 차원 반대 (정반대, 유사도 -1.0) -> 제일 낮아야 함
        saveJobCode("JOB005", "Anti-Job", v -> v[0] = -1.0f);

        // when
        List<JobCodeSimilarityProjection> result = jobCodeRepository.findTop3SimilarJobCodes(queryVector);

        // then
        assertThat(result).hasSize(3);

        // 1순위: Software Engineer (Sim 1.0)
        assertThat(result.get(0).name()).isEqualTo("Software Engineer");
        assertThat(result.get(0).similarity()).isCloseTo(1.0, offset(0.001));

        // 2순위: Data Scientist (Sim ~0.707)
        assertThat(result.get(1).name()).isEqualTo("Data Scientist");
        assertThat(result.get(1).similarity()).isCloseTo(0.707, offset(0.001));

        // 3순위: Project Manager OR Chef (둘 다 Sim 0.0, 순서 보장 안됨. 하지만 Top 3에는 들어야 함)
        // Anti-Job은 Sim -1.0이라서 제외되어야 함
        assertThat(result.get(2).similarity()).isCloseTo(0.0, offset(0.001));
        assertThat(result.get(2).name()).isIn("Project Manager", "Chef");
    }

    private void saveJobCode(String code, String name, Consumer<float[]> vectorCustomizer) {
        float[] vector = createVector(vectorCustomizer);
        jobCodeRepository.save(new JobCode(null, code, name, vector, null));
    }

    private float[] createVector(Consumer<float[]> vectorCustomizer) {
        float[] vector = new float[512];
        vectorCustomizer.accept(vector);
        return vector;
    }
}

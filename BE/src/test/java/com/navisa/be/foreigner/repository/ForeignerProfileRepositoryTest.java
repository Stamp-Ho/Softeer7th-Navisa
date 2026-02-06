package com.navisa.be.foreigner.repository;

import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.model.entity.ForeignerSimilarity;
import com.navisa.be.foreigner.model.enums.ForeignerSearchStatus;
import com.navisa.be.foreigner.repository.ForeignerProfileRepository;
import com.navisa.be.foreigner.repository.ForeignerSimilarityRepository;
import com.navisa.be.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class ForeignerProfileRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private ForeignerProfileRepository foreignerProfileRepository;

    @Autowired
    private ForeignerSimilarityRepository foreignerSimilarityRepository;

    @Test
    @DisplayName("직무 코드 배열이 겹치는 외국인 프로필을 조회한다 (overlap operator &&)")
    void findTop10ByJobCodeMatching_shouldReturnOverlappingProfiles() {
        // given
        // 1. 외국인 프로필 및 유사도 데이터 생성
        // Target Profile: JobCodes {100, 200}
        ForeignerProfile targetProfile = new ForeignerProfile(UUID.randomUUID(), ForeignerSearchStatus.IDLE);
        foreignerProfileRepository.save(targetProfile);

        ForeignerSimilarity targetSimilarity = new ForeignerSimilarity(
                null,
                targetProfile.getId(),
                new double[] {},
                new long[] { 100L, 200L });
        foreignerSimilarityRepository.save(targetSimilarity);

        // Non-Target Profile: JobCodes {300, 400}
        ForeignerProfile nonTargetProfile = new ForeignerProfile(UUID.randomUUID(), ForeignerSearchStatus.IDLE);
        foreignerProfileRepository.save(nonTargetProfile);

        ForeignerSimilarity nonTargetSimilarity = new ForeignerSimilarity(
                null,
                nonTargetProfile.getId(),
                new double[] {},
                new long[] { 300L, 400L });
        foreignerSimilarityRepository.save(nonTargetSimilarity);

        // when
        // Search Filter: {200, 500} -> 200 overlaps with Target
        long[] searchJobIds = { 200L, 500L };
        List<ForeignerProfile> result = foreignerProfileRepository.findTop10ByJobCodeMatching(searchJobIds);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(targetProfile.getId());
    }

    @Test
    @DisplayName("겹치는 직무 코드가 없으면 빈 리스트를 반환한다")
    void findTop10ByJobCodeMatching_shouldReturnEmpty_whenNoOverlap() {
        // given
        ForeignerProfile profile = new ForeignerProfile(UUID.randomUUID(), ForeignerSearchStatus.IDLE);
        foreignerProfileRepository.save(profile);

        ForeignerSimilarity similarity = new ForeignerSimilarity(
                null,
                profile.getId(),
                new double[] {},
                new long[] { 100L, 200L });
        foreignerSimilarityRepository.save(similarity);

        // when
        long[] searchJobIds = { 300L, 400L };
        List<ForeignerProfile> result = foreignerProfileRepository.findTop10ByJobCodeMatching(searchJobIds);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("최대 10개까지만 조회되어야 한다")
    void findTop10ByJobCodeMatching_shouldLimitTo10() {
        // given
        long[] commonIds = { 100L };

        // 12 records matching id 100
        IntStream.range(0, 12).forEach(i -> {
            ForeignerProfile p = new ForeignerProfile(UUID.randomUUID(), ForeignerSearchStatus.IDLE);
            foreignerProfileRepository.save(p);

            ForeignerSimilarity s = new ForeignerSimilarity(
                    null,
                    p.getId(),
                    new double[] {},
                    commonIds);
            foreignerSimilarityRepository.save(s);
        });

        // when
        List<ForeignerProfile> result = foreignerProfileRepository.findTop10ByJobCodeMatching(commonIds);

        // then
        assertThat(result).hasSize(10);
    }

    @Test
    @DisplayName("결과는 최신순(createdAt Desc)으로 정렬되어야 한다")
    void findTop10ByJobCodeMatching_shouldOrderByCreatedAtDesc() throws InterruptedException {
        // given
        long[] commonIds = { 100L };

        // Save 3 profiles sequentially with a small delay to ensure different
        // timestamps
        ForeignerProfile p1 = new ForeignerProfile(UUID.randomUUID(), ForeignerSearchStatus.IDLE);
        foreignerProfileRepository.save(p1);
        ForeignerSimilarity s1 = new ForeignerSimilarity(null, p1.getId(), new double[] {}, commonIds);
        foreignerSimilarityRepository.save(s1);

        Thread.sleep(100);

        ForeignerProfile p2 = new ForeignerProfile(UUID.randomUUID(), ForeignerSearchStatus.IDLE);
        foreignerProfileRepository.save(p2);
        ForeignerSimilarity s2 = new ForeignerSimilarity(null, p2.getId(), new double[] {}, commonIds);
        foreignerSimilarityRepository.save(s2);

        Thread.sleep(100);

        ForeignerProfile p3 = new ForeignerProfile(UUID.randomUUID(), ForeignerSearchStatus.IDLE);
        foreignerProfileRepository.save(p3);
        ForeignerSimilarity s3 = new ForeignerSimilarity(null, p3.getId(), new double[] {}, commonIds);
        foreignerSimilarityRepository.save(s3);

        // when
        List<ForeignerProfile> result = foreignerProfileRepository.findTop10ByJobCodeMatching(commonIds);

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getId()).isEqualTo(p3.getId()); // Newest first
        assertThat(result.get(1).getId()).isEqualTo(p2.getId());
        assertThat(result.get(2).getId()).isEqualTo(p1.getId()); // Oldest last
    }

    @Test
    @DisplayName("직무 코드가 null이거나 비어있으면 전체 리스트를 반환한다 (필터 무시)")
    void findTop10ByJobCodeMatching_shouldReturnAll_whenJobIdsNullOrEmpty() {
        // given
        ForeignerProfile p = new ForeignerProfile(UUID.randomUUID(), ForeignerSearchStatus.IDLE);
        foreignerProfileRepository.save(p);

        ForeignerSimilarity s = new ForeignerSimilarity(null, p.getId(), new double[] {}, new long[] { 100L });
        foreignerSimilarityRepository.save(s);

        // when & then
        assertThat(foreignerProfileRepository.findTop10ByJobCodeMatching(null)).hasSize(1);
        assertThat(foreignerProfileRepository.findTop10ByJobCodeMatching(new long[] {})).hasSize(1);
    }
}

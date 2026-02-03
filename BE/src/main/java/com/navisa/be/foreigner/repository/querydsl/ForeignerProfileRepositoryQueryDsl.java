package com.navisa.be.foreigner.repository.querydsl;

import com.navisa.be.foreigner.model.entity.ForeignerProfile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ForeignerProfileRepositoryQueryDsl {
    List<ForeignerProfile> findTop10ByJobCodeMatching(long[] jobIds);
    Optional<ForeignerProfile> findByUserIdWithNationalitiesAndLanguages(UUID userId);
}

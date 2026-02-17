package com.navisa.be.foreigner.repository.querydsl;

import com.navisa.be.foreigner.dto.request.ForeignerCardQuery;
import com.navisa.be.global.web.request.SliceRequest;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ForeignerProfileQueryDsl {

    List<ForeignerProfile> findTop10ByJobCodeMatching(long[] jobIds);
    Optional<ForeignerProfile> findByUserIdWithNationalitiesAndLanguages(UUID userId);
    List<ForeignerProfile> findByFilters(ForeignerCardQuery dto, SliceRequest<UUID> slice);
}

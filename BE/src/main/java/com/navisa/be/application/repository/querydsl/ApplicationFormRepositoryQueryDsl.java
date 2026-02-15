package com.navisa.be.application.repository.querydsl;

import com.navisa.be.application.dto.projection.VisaApplicationFormProjection;
import com.navisa.be.global.web.request.SliceRequest;

import java.util.List;
import java.util.UUID;

public interface ApplicationFormRepositoryQueryDsl {
    List<VisaApplicationFormProjection> findAllByNoOffsetAndFilter(UUID agentId, SliceRequest<UUID> slice, Boolean complete);
}

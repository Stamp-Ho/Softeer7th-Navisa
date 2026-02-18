package com.navisa.be.application.repository.querydsl;

import com.navisa.be.application.dto.projection.ApplicationFormProjection;
import com.navisa.be.global.web.request.SliceRequest;

import java.util.List;
import java.util.UUID;

public interface ApplicationFormQueryDsl {
    List<ApplicationFormProjection> findAllByNoOffsetAndFilter(UUID agentId, SliceRequest<UUID> slice, Boolean complete);
}

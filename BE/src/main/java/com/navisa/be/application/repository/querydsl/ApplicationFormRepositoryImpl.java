package com.navisa.be.application.repository.querydsl;

import com.navisa.be.application.dto.projection.VisaApplicationFormProjection;
import com.navisa.be.application.dto.response.VisaApplicationCardResponse;
import com.navisa.be.application.model.entity.VisaApplicationForm;
import com.navisa.be.common.dto.request.SliceRequest;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.navisa.be.application.model.entity.QVisaApplicationForm.visaApplicationForm;
import static com.navisa.be.foreigner.model.entity.QForeignerProfile.foreignerProfile;

@RequiredArgsConstructor
public class ApplicationFormRepositoryImpl implements ApplicationFormRepositoryQueryDsl{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<VisaApplicationFormProjection> findAllByNoOffsetAndFilter(UUID agentId, SliceRequest<UUID> slice, Boolean complete) {

        LocalDateTime lastElementModifiedAt = null;
        if (slice.lastElementId() != null) {
            lastElementModifiedAt = queryFactory
                    .select(visaApplicationForm.updatedAt)
                    .from(visaApplicationForm)
                    .where(visaApplicationForm.id.eq(slice.lastElementId()))
                    .fetchOne();
        }

        return queryFactory
                .select(Projections.constructor(VisaApplicationFormProjection.class,
                        visaApplicationForm.id,
                        foreignerProfile.nickname,
                        visaApplicationForm.currentStep,
                        visaApplicationForm.totalCount,
                        visaApplicationForm.profileObjectKey,
                        visaApplicationForm.updatedAt
                ))
                .from(visaApplicationForm)
                .leftJoin(visaApplicationForm.foreignerProfile, foreignerProfile)
                .where(
                        cursorCondition(lastElementModifiedAt, slice.lastElementId()),
                        isDone(complete),
                        isOwnedByAgentId(agentId)
                )
                .orderBy(visaApplicationForm.updatedAt.desc(), visaApplicationForm.id.asc())
                .limit(slice.size() + 1)
                .fetch();
    }

    /**
     * updated_at 기준 No-Offset 커서 조건
     * 정렬 기준: updated_at DESC, id ASC
     */
    private BooleanExpression cursorCondition(LocalDateTime lastModifiedAt, UUID lastId) {
        if (lastModifiedAt == null || lastId == null) {
            return null; // 첫 페이지 조회 시 null 반환
        }

        // (수정일 < 마지막수정일) OR (수정일 == 마지막수정일 AND ID > 마지막ID)
        return visaApplicationForm.updatedAt.lt(lastModifiedAt)
                .or(visaApplicationForm.updatedAt.eq(lastModifiedAt)
                        .and(visaApplicationForm.id.gt(lastId)));
    }

    private BooleanExpression isDone(Boolean complete) { // complete = true일 때 isDone = true인 경우 조회
        if (complete == null) {
            return null;
        }

        return visaApplicationForm.isDone.eq(complete);
    }

    private BooleanExpression isOwnedByAgentId(UUID agentId) {
        if (agentId == null) {
            return Expressions.asBoolean(false).isTrue(); // 혹여나 agentId가 null일 시, 아무것도 조회안되게 제약
        }

        return visaApplicationForm.agentProfile.id.eq(agentId);
    }
}

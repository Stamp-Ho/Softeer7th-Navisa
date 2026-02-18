package com.navisa.be.application.repository.querydsl;

import com.navisa.be.application.dto.projection.ApplicationFormProjection;
import com.navisa.be.global.web.request.SliceRequest;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.navisa.be.application.model.entity.QApplicationForm.applicationForm;
import static com.navisa.be.foreigner.model.entity.QForeignerProfile.foreignerProfile;

@RequiredArgsConstructor
public class ApplicationFormQueryDslImpl implements ApplicationFormQueryDsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ApplicationFormProjection> findAllByNoOffsetAndFilter(UUID agentId, SliceRequest<UUID> slice, Boolean complete) {

        LocalDateTime lastElementModifiedAt = null;
        if (slice.lastElementId() != null) {
            lastElementModifiedAt = queryFactory
                    .select(applicationForm.updatedAt)
                    .from(applicationForm)
                    .where(applicationForm.id.eq(slice.lastElementId()))
                    .fetchOne();
        }

        return queryFactory
                .select(Projections.constructor(ApplicationFormProjection.class,
                        applicationForm.id,
                        foreignerProfile.nickname,
                        applicationForm.currentStep,
                        applicationForm.totalCount,
                        applicationForm.profileObjectKey,
                        applicationForm.updatedAt
                ))
                .from(applicationForm)
                .leftJoin(applicationForm.foreignerProfile, foreignerProfile)
                .where(
                        cursorCondition(lastElementModifiedAt, slice.lastElementId()),
                        isDone(complete),
                        isOwnedByAgentId(agentId)
                )
                .orderBy(applicationForm.updatedAt.desc(), applicationForm.id.asc())
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
        return applicationForm.updatedAt.lt(lastModifiedAt)
                .or(applicationForm.updatedAt.eq(lastModifiedAt)
                        .and(applicationForm.id.gt(lastId)));
    }

    private BooleanExpression isDone(Boolean complete) { // complete = true일 때 isDone = true인 경우 조회
        if (complete == null) {
            return null;
        }

        return applicationForm.isDone.eq(complete);
    }

    private BooleanExpression isOwnedByAgentId(UUID agentId) {
        if (agentId == null) {
            return Expressions.asBoolean(false).isTrue(); // 혹여나 agentId가 null일 시, 아무것도 조회안되게 제약
        }

        return applicationForm.agentProfile.id.eq(agentId);
    }
}

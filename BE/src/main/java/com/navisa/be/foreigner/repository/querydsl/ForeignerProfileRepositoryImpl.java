package com.navisa.be.foreigner.repository.querydsl;

import com.navisa.be.common.dto.request.SliceRequest;
import com.navisa.be.foreigner.dto.ForeignerCardQueryDto;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.model.enums.ForeignerSearchStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.navisa.be.common.model.entity.QLanguage.language;
import static com.navisa.be.common.model.entity.QNationality.nationality;
import static com.navisa.be.foreigner.model.entity.QForeignerLanguage.foreignerLanguage;
import static com.navisa.be.foreigner.model.entity.QForeignerNationality.foreignerNationality;
import static com.navisa.be.foreigner.model.entity.QForeignerProfile.foreignerProfile;
import static com.navisa.be.foreigner.model.entity.QForeignerSimilarity.foreignerSimilarity;

@RequiredArgsConstructor
public class ForeignerProfileRepositoryImpl implements ForeignerProfileRepositoryQueryDsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ForeignerProfile> findTop10ByJobCodeMatching(long[] jobIds) {
        return queryFactory
                .selectFrom(foreignerProfile)
                // 1. Similarity 테이블과 조인
                .innerJoin(foreignerSimilarity).on(foreignerProfile.id.eq(foreignerSimilarity.foreignerId))
                // 2. default_batch_size으로 연관 데이터 조회에 대한 N + 1 방지
                .leftJoin(foreignerProfile.foreignerNationalities, foreignerNationality)
                .leftJoin(foreignerNationality.nationality, nationality)
                .leftJoin(foreignerProfile.foreignLanguages, foreignerLanguage)
                .leftJoin(foreignerLanguage.language, language)
                // 3. PostgreSQL 배열 오버랩 연산자 (&&) 처리
                .where(overlapJobIds(jobIds))
                // 4. 최신 등록순 기준으로 정렬
                .orderBy(foreignerProfile.createdAt.desc())
                .limit(10)
                .distinct() // 중복 데이터 제거
                .fetch();
    }

    @Override
    public Optional<ForeignerProfile> findByUserIdWithNationalitiesAndLanguages(UUID userId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(foreignerProfile)
                        // 국적 정보 Fetch Join
                        .leftJoin(foreignerProfile.foreignerNationalities, foreignerNationality).fetchJoin()
                        .leftJoin(foreignerNationality.nationality, nationality).fetchJoin()
                        // 언어 정보 Fetch Join
                        .leftJoin(foreignerProfile.foreignLanguages, foreignerLanguage).fetchJoin()
                        .leftJoin(foreignerLanguage.language, language).fetchJoin()
                        .where(foreignerProfile.userId.eq(userId))
                        .fetchOne());
    }

    @Override
    public List<ForeignerProfile> findByFilters(ForeignerCardQueryDto dto, SliceRequest<UUID> slice) {

        LocalDateTime lastElementCreatedAt = null;
        if (slice.lastElementId() != null) {
            lastElementCreatedAt = queryFactory
                    .select(foreignerProfile.createdAt)
                    .from(foreignerProfile)
                    .where(foreignerProfile.id.eq(slice.lastElementId()))
                    .fetchOne();
        }

        JPAQuery<ForeignerProfile> query = queryFactory
                .selectFrom(foreignerProfile)
                .distinct()
                .innerJoin(foreignerSimilarity).on(foreignerProfile.id.eq(foreignerSimilarity.foreignerId)) // 필수 조인
                .where(
                        isIdle(),
                        cursorCondition(lastElementCreatedAt, slice.lastElementId()),
                        overlapJobIds(dto.jobIdList().stream().mapToLong(Long::longValue).toArray()));

        // [최적화] 조건이 있을 때만 Join & Where 적용
        if (dto.nationIdList() != null && !dto.nationIdList().isEmpty()) {
            query.leftJoin(foreignerProfile.foreignerNationalities, foreignerNationality)
                    .where(nationIdIn(dto.nationIdList()));
        }
        if (dto.languageIdList() != null && !dto.languageIdList().isEmpty()) {
            query.leftJoin(foreignerProfile.foreignLanguages, foreignerLanguage)
                    .where(languageIdIn(dto.languageIdList()));
        }
        return query
                .orderBy(foreignerProfile.createdAt.desc(), foreignerProfile.id.asc())
                .limit(slice.size() + 1)
                .fetch();
    }

    private BooleanExpression isIdle() {
        return foreignerProfile.status.eq(ForeignerSearchStatus.IDLE);
    }

    /**
     * PostgreSQL의 '&&' 연산자를 사용하여 배열 간의 교집합이 있는지 확인하는 템플릿
     */
    private BooleanExpression overlapJobIds(long[] jobIds) {
        if (jobIds == null || jobIds.length == 0) {
            return null;
        }
        // {1, 2, 3} 형태의 문자열로 변환하여 템플릿에 전달
        return Expressions.booleanTemplate(
                "function('array_overlap', {0}, {1}) = true",
                foreignerSimilarity.jobCodeIdList,
                jobIds);
    }

    /**
     * created_at 기준 No-Offset 커서 조건
     * 정렬 기준: created_at DESC, id ASC
     */
    private BooleanExpression cursorCondition(LocalDateTime lastCreatedAt, UUID lastId) {
        if (lastCreatedAt == null || lastId == null) {
            return null; // 첫 페이지 조회 시 null 반환
        }

        // (생성일 < 마지막생성일) OR (생성일 == 마지막생성일 AND ID > 마지막ID)
        return foreignerProfile.createdAt.lt(lastCreatedAt)
                .or(foreignerProfile.createdAt.eq(lastCreatedAt)
                        .and(foreignerProfile.id.gt(lastId)));
    }

    private BooleanExpression languageIdIn(List<Long> languageIds) {
        return (languageIds == null || languageIds.isEmpty()) ? null : foreignerLanguage.language.id.in(languageIds);
    }

    private BooleanExpression nationIdIn(List<Long> nationIds) {
        return (nationIds == null || nationIds.isEmpty()) ? null : foreignerNationality.nationality.id.in(nationIds);
    }
}

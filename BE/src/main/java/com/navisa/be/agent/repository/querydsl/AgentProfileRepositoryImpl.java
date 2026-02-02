package com.navisa.be.agent.repository.querydsl;

import com.navisa.be.agent.dto.request.AgentCardRequest;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.model.enums.OfficeAddressRegion;
import com.navisa.be.common.dto.request.SliceRequest;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import static com.navisa.be.agent.model.entity.QAgentLanguage.agentLanguage;
import static com.navisa.be.agent.model.entity.QAgentProfile.agentProfile;
import static com.navisa.be.agent.model.entity.QAgentSpecializedJob.agentSpecializedJob;

@RequiredArgsConstructor
public class AgentProfileRepositoryImpl implements AgentProfileRepositoryQueryDsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<AgentProfile> findByFilters(AgentCardRequest request, SliceRequest<UUID> slice) {

        // 1. 커서 기준점(마지막으로 본 행정사의 activeScore) 사전 조회
        Double lastActiveScore = null;
        if (slice.lastElementId() != null) {
            lastActiveScore = queryFactory
                    .select(agentProfile.activeScore)
                    .from(agentProfile)
                    .where(agentProfile.id.eq(slice.lastElementId()))
                    .fetchOne();
        }

        // 2. 메인 쿼리 실행 (상수화된 lastActiveScore 전달)
        return queryFactory
                .selectFrom(agentProfile)
                .distinct()
                .leftJoin(agentProfile.specializedJobCodes, agentSpecializedJob)
                .leftJoin(agentProfile.languages, agentLanguage)
                .where(
                        cursorCondition(lastActiveScore, slice.lastElementId()),
                        jobIdIn(request.jobIdList()),
                        languageIdIn(request.languageIdList()),
                        regionIn(request.regionList()))
                .orderBy(agentProfile.activeScore.desc(), agentProfile.id.asc())
                .limit(slice.size() + 1)
                .fetch();
    }

    /**
     * activeScore 기준 No-Offset 커서 조건 (상수 비교 방식)
     * 정렬 기준: activeScore DESC, id ASC
     */
    private BooleanExpression cursorCondition(Double lastActiveScore, UUID lastId) {
        if (lastActiveScore == null || lastId == null) {
            return null;
        }

        // (점수 < 마지막점수) OR (점수 == 마지막점수 AND ID > 마지막ID)
        return agentProfile.activeScore.lt(lastActiveScore)
                .or(agentProfile.activeScore.eq(lastActiveScore).and(agentProfile.id.gt(lastId)));
    }

    private BooleanExpression jobIdIn(List<Long> jobIds) {
        return (jobIds == null || jobIds.isEmpty()) ? null : agentSpecializedJob.jobCode.id.in(jobIds);
    }

    private BooleanExpression languageIdIn(List<Long> languageIds) {
        return (languageIds == null || languageIds.isEmpty()) ? null : agentLanguage.language.id.in(languageIds);
    }

    private BooleanExpression regionIn(List<String> regions) {
        if (regions == null || regions.isEmpty())
            return null;

        List<String> expandedKeywords = OfficeAddressRegion.getAllSearchKeywords(regions);
        List<String> searchKeywords = expandedKeywords.isEmpty() ? regions : expandedKeywords;

        return searchKeywords.stream()
                .map(agentProfile.officeAddress::contains)
                .reduce(BooleanExpression::or)
                .orElse(null);
    }
}

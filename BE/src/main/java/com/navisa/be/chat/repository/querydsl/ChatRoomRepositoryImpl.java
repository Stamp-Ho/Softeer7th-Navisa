package com.navisa.be.chat.repository.querydsl;

import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ChatRoomFilterType;
import com.navisa.be.chat.model.enums.ProposalStatus;
import com.navisa.be.global.web.request.SliceRequest;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static com.navisa.be.agent.model.entity.QAgentProfile.agentProfile;
import static com.navisa.be.chat.model.entity.QChatMessage.chatMessage;
import static com.navisa.be.chat.model.entity.QChatRoom.chatRoom;
import static com.navisa.be.chat.model.entity.QProposal.proposal;
import static com.navisa.be.foreigner.model.entity.QForeignerProfile.foreignerProfile;


@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomRepositoryQueryDsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ChatRoom> findByNoOffset(UUID profileId, SliceRequest<Long> slice, boolean isForeignerId, ChatRoomFilterType filter) {
        // 커서 데이터 조회
        ZonedDateTime lastChattedAt = null;
        if (slice.lastElementId() != null) {
            lastChattedAt = queryFactory
                    .select(chatRoom.lastChattedAt)
                    .from(chatRoom)
                    .where(chatRoom.id.eq(slice.lastElementId()))
                    .fetchOne();
        }

        // 기본 쿼리 생성
        JPAQuery<ChatRoom> query = queryFactory.selectFrom(chatRoom)
                .where(
                        cursorCondition(lastChattedAt, slice.lastElementId()),
                        profileIdEq(profileId, isForeignerId), // 프로필 조건 통합
                        filterCondition(profileId, filter) // 필터 조건 추가
                );

        // Fetch Join 처리
        if (isForeignerId) {
            query.leftJoin(chatRoom.agentProfile, agentProfile).fetchJoin();
        } else {
            query.leftJoin(chatRoom.foreignerProfile, foreignerProfile).fetchJoin();
        }

        return query
                .orderBy(chatRoom.lastChattedAt.desc(), chatRoom.id.asc())
                .limit(slice.size() + 1)
                .fetch();
    }

    /**
     * 필터링을 위한 서브 쿼리
     */
    private BooleanExpression filterCondition(UUID profileId, ChatRoomFilterType filterType) {
        if (filterType == null || filterType == ChatRoomFilterType.ALL) {
            return null;
        }

        // 해당 채팅방에 profileId가 아닌 사람이 보낸 안읽은 메시지가 있는지 확인
        if (filterType == ChatRoomFilterType.UNREAD) {
            return JPAExpressions
                    .selectOne()
                    .from(chatMessage)
                    .where(
                            chatMessage.chatRoom.eq(chatRoom),
                            chatMessage.senderId.ne(profileId),
                            chatMessage.isReadByOther.isFalse()
                    )
                    .exists();
        }

        // 해당 채팅방에 수임중인 Proposal이 있는지 확인
        if (filterType == ChatRoomFilterType.MATCHED) {
            return JPAExpressions
                    .selectOne()
                    .from(proposal)
                    .where(
                            proposal.chatRoom.eq(chatRoom),
                            proposal.status.eq(ProposalStatus.MATCHED)
                    )
                    .exists();
        }

        return null;
    }

    /**
     * 프로필 ID 조건
     */
    private BooleanExpression profileIdEq(UUID profileId, boolean isForeignerId) {
        return isForeignerId ? chatRoom.foreignerProfile.id.eq(profileId) : chatRoom.agentProfile.id.eq(profileId);
    }

    /**
     * lastChattedAt 기준 No-Offset 커서 조건 (상수 비교 방식)
     * 정렬 기준: lastChattedAt DESC, id ASC
     */
    private BooleanExpression cursorCondition(ZonedDateTime lastChattedAt, Long lastId) {
        if (lastChattedAt == null || lastId == null) {
            return null;
        }

        return chatRoom.lastChattedAt.lt(lastChattedAt)
                .or(chatRoom.lastChattedAt.eq(lastChattedAt)
                        .and(chatRoom.id.gt(lastId)));
    }
}

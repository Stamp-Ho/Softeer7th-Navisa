package com.navisa.be.chat.repository.querydsl;

import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.common.dto.request.SliceRequest;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static com.navisa.be.agent.model.entity.QAgentProfile.agentProfile;
import static com.navisa.be.chat.model.entity.QChatRoom.chatRoom;
import static com.navisa.be.foreigner.model.entity.QForeignerProfile.foreignerProfile;


@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomRepositoryQueryDsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ChatRoom> findByNoOffset(UUID profileId, SliceRequest<Long> slice, boolean isForeignerId) {
        ZonedDateTime lastChattedAt = null;
        if (slice.lastElementId() != null) {
            lastChattedAt = queryFactory
                    .select(chatRoom.lastChattedAt)
                    .from(chatRoom)
                    .where(chatRoom.id.eq(slice.lastElementId()))
                    .fetchOne();
        }

        JPAQuery<ChatRoom> query = queryFactory.select(chatRoom)
                .from(chatRoom)
                .where(cursorCondition(lastChattedAt, slice.lastElementId()));

        if (isForeignerId) {
            query
                    .leftJoin(chatRoom.foreignerProfile, foreignerProfile).fetchJoin()
                    .where(
                            isForeignerIdEq(profileId)
                    );
        } else {
            query
                    .leftJoin(chatRoom.agentProfile, agentProfile).fetchJoin()
                    .where(
                            isAgentIdEq(profileId)
                    );
        }

        return query
                .orderBy(chatRoom.lastChattedAt.desc(), chatRoom.id.asc())
                .limit(slice.size())
                .fetch();
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

    private BooleanExpression isForeignerIdEq(UUID profileId) {
        return chatRoom.foreignerProfile.id.eq(profileId);
    }

    private BooleanExpression isAgentIdEq(UUID profileId) {
        return chatRoom.agentProfile.id.eq(profileId);
    }
}

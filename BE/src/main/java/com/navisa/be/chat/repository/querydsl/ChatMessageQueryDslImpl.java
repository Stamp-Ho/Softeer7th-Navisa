package com.navisa.be.chat.repository.querydsl;

import com.navisa.be.chat.model.entity.ChatMessage;
import com.navisa.be.chat.model.enums.ProposalStatus;
import com.navisa.be.global.web.request.SliceRequest;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.navisa.be.chat.model.entity.QChatMessage.chatMessage;
import static com.navisa.be.chat.model.entity.QChatRoom.chatRoom;
import static com.navisa.be.chat.model.entity.QProposal.proposal;

@RequiredArgsConstructor
public class ChatMessageQueryDslImpl implements ChatMessageQueryDsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public Long findNonReadCountByProfileId(UUID profileId, boolean isForeigner) {
        return queryFactory
                .select(chatMessage.count())
                .from(chatMessage)
                .join(chatMessage.chatRoom, chatRoom) // count() 쿼리를 날리기에 fetchJoin을 쓰지 않음
                .where(
                        profileMatchCondition(profileId, isForeigner), // 내가 속한 채팅방 필터링
                        chatMessage.senderId.ne(profileId), // 내가 보낸 게 아닌 메시지
                        chatMessage.isReadByOther.isFalse() // 아직 읽지 않은 메시지
                )
                .fetchOne();
    }

    @Override
    public Long findMatchedNonReadCountByAgentId(UUID agentId) {
        return queryFactory
                .select(chatMessage.count())
                .from(chatMessage)
                .join(chatMessage.chatRoom, chatRoom) // count() 쿼리를 날리기에 fetchJoin을 쓰지 않음
                .join(proposal).on(proposal.chatRoom.eq(chatRoom))
                .where(
                        chatRoom.agentProfile.id.eq(agentId),
                        proposal.status.eq(ProposalStatus.MATCHED),
                        chatMessage.senderId.ne(agentId),
                        chatMessage.isReadByOther.isFalse())
                .fetchOne();
    }

    @Override
    public List<ChatMessage> findChatMessagesByChatRoomIdAndNoOffset(Long chatRoomId, SliceRequest<Long> slice) {

        LocalDateTime lastElementCreatedAt = null;
        if (slice.lastElementId() != null) {
            lastElementCreatedAt = queryFactory
                    .select(chatMessage.createdAt)
                    .from(chatMessage)
                    .where(chatMessage.id.eq(slice.lastElementId()))
                    .fetchOne();
        }

        return queryFactory
                .selectFrom(chatMessage)
                .where(
                        cursorCondition(lastElementCreatedAt, slice.lastElementId()),
                        isChatRoom(chatRoomId)
                )
                .orderBy(chatMessage.createdAt.desc(), chatMessage.id.asc())
                .limit(slice.size() + 1)
                .fetch();
    }

    /**
     * 사용자의 역할(외국인/행정사)에 따라 채팅방 참여 여부를 확인하는 조건문
     */
    private BooleanExpression profileMatchCondition(UUID profileId, boolean isForeigner) {
        return isForeigner
                ? chatRoom.foreignerProfile.id.eq(profileId)
                : chatRoom.agentProfile.id.eq(profileId);
    }

    /**
     * created_at 기준 No-Offset 커서 조건
     * 정렬 기준: created_at DESC, id ASC
     */
    private BooleanExpression cursorCondition(LocalDateTime lastCreatedAt, Long lastId) {
        if (lastCreatedAt == null || lastId == null) {
            return null; // 첫 페이지 조회 시 null 반환
        }

        // (생성일 < 마지막생성일) OR (생성일 == 마지막생성일 AND ID > 마지막ID)
        return chatMessage.createdAt.lt(lastCreatedAt)
                .or(chatMessage.createdAt.eq(lastCreatedAt)
                        .and(chatMessage.id.gt(lastId)));
    }

    private BooleanExpression isChatRoom(Long chatRoomId) {
        return chatMessage.chatRoom.id.eq(chatRoomId);
    }
}

package com.navisa.be.chat.repository.querydsl;

import com.navisa.be.chat.model.enums.ProposalStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import static com.navisa.be.chat.model.entity.QChatMessage.chatMessage;
import static com.navisa.be.chat.model.entity.QChatRoom.chatRoom;
import static com.navisa.be.chat.model.entity.QProposal.proposal;

@RequiredArgsConstructor
public class ChatMessageRepositoryImpl implements ChatMessageRepositoryQueryDsl{

    private final JPAQueryFactory queryFactory;

    @Override
    public Long findNonReadCountByProfileId(UUID profileId, boolean isForeigner) {
        return queryFactory
                .select(chatMessage.count())
                .from(chatMessage)
                .join(chatMessage.chatRoom, chatRoom) // count() 쿼리를 날리기에 fetchJoin을 쓰지 않음
                .where(
                        profileMatchCondition(profileId, isForeigner), // 내가 속한 채팅방 필터링
                        chatMessage.senderId.ne(profileId),            // 내가 보낸 게 아닌 메시지
                        chatMessage.isReadByOther.isFalse()            // 아직 읽지 않은 메시지
                )
                .fetchOne();
    }

    @Override
    public Long findMatchedNonReadCountByAgentId(UUID agentId) {
        return queryFactory
                .select(chatMessage.count())
                .from(chatMessage)
                .join(chatMessage.chatRoom, chatRoom)  // count() 쿼리를 날리기에 fetchJoin을 쓰지 않음
                .join(proposal).on(proposal.chatRoom.eq(chatRoom))
                .where(
                        chatRoom.agentProfile.id.eq(agentId),
                        proposal.status.eq(ProposalStatus.MATCHED),
                        chatMessage.senderId.ne(agentId),
                        chatMessage.isReadByOther.isFalse()
                )
                .fetchOne();
    }

    /**
     * 사용자의 역할(외국인/행정사)에 따라 채팅방 참여 여부를 확인하는 조건문
     */
    private BooleanExpression profileMatchCondition(UUID profileId, boolean isForeigner) {
        return isForeigner
                ? chatRoom.foreignerProfile.id.eq(profileId)
                : chatRoom.agentProfile.id.eq(profileId);
    }
}

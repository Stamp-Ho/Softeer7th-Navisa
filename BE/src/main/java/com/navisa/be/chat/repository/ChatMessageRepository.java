package com.navisa.be.chat.repository;

import com.navisa.be.chat.dto.projection.ChatMessageNonReadCountProjection;
import com.navisa.be.chat.dto.projection.LastMessageProjection;
import com.navisa.be.chat.model.entity.ChatMessage;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.repository.querydsl.ChatMessageQueryDsl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>, ChatMessageQueryDsl {

    @Query(value = """
                SELECT cm.chat_message_id as id, cm.chat_room_id as chatRoomId, cm.content as content
                FROM chat_message cm
                WHERE (cm.chat_room_id, cm.created_at, cm.chat_message_id) IN (
                    SELECT sub.chat_room_id, sub.created_at, sub.chat_message_id
                    FROM (
                        SELECT chat_room_id, created_at, chat_message_id,
                               ROW_NUMBER() OVER (
                                   PARTITION BY chat_room_id
                                   ORDER BY created_at DESC, chat_message_id DESC
                               ) as rn
                        FROM chat_message
                        WHERE chat_room_id IN :chatRoomIds
                    ) sub
                    WHERE sub.rn = 1
                )
            """, nativeQuery = true)
    List<LastMessageProjection> findAllLastChatMessageGroupByChatRoom(@Param("chatRoomIds") List<Long> chatRoomIds);

    @Query("""
                SELECT cm.chatRoom.id AS id, COUNT(cm) AS count
                FROM ChatMessage cm
                WHERE cm.chatRoom IN :chatRooms
                  AND cm.isReadByOther = false
                  AND cm.senderId != :profileId
                GROUP BY cm.chatRoom.id
            """)
    List<ChatMessageNonReadCountProjection> findCountByChatRoomIn(
            @Param("chatRooms") List<ChatRoom> chatRooms, @Param("profileId") UUID profileId);

    @Query("""
                SELECT cm.chatRoom.id AS id, COUNT(cm) AS count
                FROM ChatMessage cm
                WHERE cm.chatRoom.id IN :chatRoomIds
                  AND cm.isReadByOther = false
                  AND cm.senderId != :profileId
                GROUP BY cm.chatRoom.id
            """)
    List<ChatMessageNonReadCountProjection> findCountByChatRoomIdsIn(
            @Param("chatRoomIds") List<Long> chatRoomIds, @Param("profileId") UUID profileId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
                UPDATE ChatMessage m
                    SET m.isReadByOther = true
                    WHERE m.chatRoom.id = :chatRoomId
                      AND m.senderId != :profileId
                      AND m.isReadByOther = false
                      AND (m.createdAt, m.id) <= (
                          SELECT m2.createdAt, m2.id
                          FROM ChatMessage m2
                          WHERE m2.id = :chatMessageId
                      )
            """)
    void updateReadStatusBeforeChatMessageSentAt(@Param("chatMessageId") Long chatMessageId,
                                                 @Param("profileId") UUID profileId,
                                                 @Param("chatRoomId") Long chatRoomId);
}

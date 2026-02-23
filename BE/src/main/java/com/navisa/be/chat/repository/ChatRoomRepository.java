package com.navisa.be.chat.repository;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.repository.querydsl.ChatRoomQueryDsl;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.Optional;
import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, ChatRoomQueryDsl {

    @Query(value = """
                SELECT * FROM chat_room c
                WHERE c.agent_id = :agentId
                  AND c.foreigner_id = :foreignerId
                ORDER BY c.chat_room_id DESC
                LIMIT 1
            """, nativeQuery = true)
    Optional<ChatRoom> findByAgentIdAndForeignerId(
            @Param("agentId") UUID agentId,
            @Param("foreignerId") UUID foreignerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
                SELECT cr
                FROM ChatRoom cr
                JOIN FETCH cr.agentProfile
                JOIN FETCH cr.foreignerProfile
                WHERE cr.id = :roomId
            """)
    Optional<ChatRoom> findByIdWithLock(@Param("roomId") Long roomId);

    @Query("""
                SELECT cr
                FROM ChatRoom cr
                JOIN FETCH cr.agentProfile
                JOIN FETCH cr.foreignerProfile
                WHERE cr.id = :roomId
            """)
    Optional<ChatRoom> findByIdWithProfiles(@Param("roomId") Long roomId);

    boolean existsByAgentProfileIdAndForeignerProfileId(UUID agentId, UUID foreignerId);

    @Query("SELECT cr FROM ChatRoom cr " +
            "JOIN FETCH cr.agentProfile " +
            "JOIN FETCH cr.foreignerProfile fp " +
            "LEFT JOIN FETCH fp.foreignerNationalities fn " +
            "LEFT JOIN FETCH fn.nationality " +
            "WHERE cr.id = :roomId")
    Optional<ChatRoom> findByIdWithParticipantsInfo(@Param("roomId") Long roomId);

    Optional<ChatRoom> findByAgentProfileAndForeignerProfile(AgentProfile agentProfile,
            ForeignerProfile foreignerProfile);
}

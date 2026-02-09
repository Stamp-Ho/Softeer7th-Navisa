package com.navisa.be.chat.repository;

import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.repository.querydsl.ChatRoomRepositoryQueryDsl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, ChatRoomRepositoryQueryDsl {

    @Query("SELECT c FROM ChatRoom c WHERE c.agentProfile.id = :agentId AND c.foreignerProfile.id = :foreignerId")
    Optional<ChatRoom> findByAgentIdAndForeignerId(UUID agentId, UUID foreignerId);

    @Query("SELECT c.id FROM ChatRoom c WHERE c.foreignerProfile.id = :foreignerProfileId")
    List<Long> findAllIdsByForeignerProfileId(UUID foreignerProfileId);

    @Query("SELECT c.id FROM ChatRoom c WHERE c.agentProfile.id = :agentProfileId")
    List<Long> findAllIdsByAgentProfileId(UUID agentProfileId);
}

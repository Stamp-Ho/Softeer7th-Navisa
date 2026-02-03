package com.navisa.be.chat.repository;

import com.navisa.be.chat.model.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT c FROM ChatRoom c WHERE c.agentProfile.id = :agentId AND c.foreignerProfile.id = :foreignerId")
    Optional<ChatRoom> findByAgentIdAndForeignerId(UUID agentId, UUID foreignerId);
}

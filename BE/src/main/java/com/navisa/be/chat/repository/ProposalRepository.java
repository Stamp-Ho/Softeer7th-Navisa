package com.navisa.be.chat.repository;

import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.entity.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {

    Optional<Proposal> findFirstByChatRoomOrderByCreatedAtDesc(ChatRoom chatRoom);
}

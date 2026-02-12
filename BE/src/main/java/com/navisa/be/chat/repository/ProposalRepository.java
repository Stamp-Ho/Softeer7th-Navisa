package com.navisa.be.chat.repository;

import com.navisa.be.chat.dto.projection.ChatRoomProposalStatusProjection;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.entity.Proposal;
import com.navisa.be.chat.model.enums.ProposalStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {

    Optional<Proposal> findFirstByChatRoomOrderByIdDesc(ChatRoom chatRoom);

    @Query("SELECT p.chatRoom.id AS chatRoomId, p.status AS status, p.senderId AS senderId " +
            "FROM Proposal p " +
            "WHERE p.id IN (" +
            "  SELECT MAX(p2.id) " +
            "  FROM Proposal p2 " +
            "  WHERE p2.chatRoom IN :chatRooms " +
            "  GROUP BY p2.chatRoom.id" +
            ")")
    List<ChatRoomProposalStatusProjection> findProposalStatusByChatRoomIn(@Param("chatRooms") Collection<ChatRoom> chatRooms);

    @Query("SELECT p FROM Proposal p " +
            "JOIN FETCH p.chatRoom " +
            "WHERE p.chatRoom.foreignerProfile.id = :foreignerId AND p.status IN :statuses " +
            "ORDER BY p.id DESC")
    List<Proposal> findLatestMatchedProposal(
            @Param("foreignerId") UUID foreignerId,
            @Param("statuses") List<ProposalStatus> statuses,
            Pageable pageable
    );

    Optional<Proposal> findFirstByChatRoom_IdOrderByIdDesc(Long chatRoomId);
}

package com.navisa.be.chat.service;

import com.navisa.be.chat.dto.projection.ChatRoomProposalStatusProjection;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ProposalStatus;
import com.navisa.be.chat.repository.ProposalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProposalService {

    private final ProposalRepository proposalRepository;

    public List<ChatRoomProposalStatusProjection> findByChatRoomIn(Collection<ChatRoom> contentChatRooms) {
        return proposalRepository.findProposalStatusByChatRoomIn(contentChatRooms);
    }

    public void updateStatusByChatRoomId(Long id) {
        proposalRepository.findFirstByChatRoom_IdOrderByIdDesc(id)
                .ifPresent(proposal -> proposal.updateStatus(ProposalStatus.REJECTED));
    }
}

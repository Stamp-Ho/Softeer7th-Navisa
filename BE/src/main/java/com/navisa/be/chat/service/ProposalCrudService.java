package com.navisa.be.chat.service;

import com.navisa.be.chat.exception.ProposalException;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.entity.Proposal;
import com.navisa.be.chat.model.enums.ProposalStatus;
import com.navisa.be.chat.repository.ProposalRepository;
import com.navisa.be.global.web.response.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProposalCrudService {

    private final ChatRoomCrudService chatRoomQueryService;
    private final ProposalRepository proposalRepository;


    @Transactional(readOnly = true)
    public Optional<Proposal> findOptionalLatestProposalByChatRoom(ChatRoom chatRoom) {
        return proposalRepository.findFirstByChatRoomOrderByIdDesc(chatRoom);
    }

    @Transactional(readOnly = true)
    public Proposal findLatestProposalByAgentIdAndForeignerId(UUID agentId, UUID foreignerId) {
        ChatRoom chatRoom = chatRoomQueryService.findOptionalByAgentIdAndForeignerId(agentId, foreignerId)
                .orElseThrow(() -> new ProposalException(ResponseStatus.NOT_FOUND_CHATROOM));

        Proposal proposal = proposalRepository.findFirstByChatRoomOrderByIdDesc(chatRoom)
                .orElseThrow(() -> new ProposalException(ResponseStatus.PROPOSAL_NOT_FOUND));

        return proposal;
    }

    @Transactional(readOnly = true)
    public Proposal findOngoingOneByForeignerId(UUID foreignerId) {
        List<Proposal> proposals = proposalRepository.findLatestMatchedProposal(
                foreignerId,
                List.of(ProposalStatus.MATCHED, ProposalStatus.COMPLETED),
                PageRequest.of(0, 1)
        );

        Proposal proposal = proposals.stream()
                .findFirst()
                .orElseThrow(() -> new ProposalException(ResponseStatus.PROPOSAL_NOT_FOUND));

        return proposal;
    }

    @Transactional(readOnly = true)
    public Optional<Proposal> findLatestMatchedProposal(UUID foreignerId) {
        List<ProposalStatus> targetStatuses = List.of(ProposalStatus.MATCHED, ProposalStatus.COMPLETED);

        List<Proposal> proposals = proposalRepository.findLatestMatchedProposal(
                foreignerId,
                targetStatuses,
                PageRequest.of(0, 1)
        );

        return proposals.stream().findFirst();
    }

    @Transactional(readOnly = true)
    public Optional<Proposal> findFirstByChatRoomOrderByIdDesc(ChatRoom chatRoom) {
        return proposalRepository.findFirstByChatRoomOrderByIdDesc(chatRoom);
    }
}

package com.navisa.be.support;

import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.entity.Proposal;
import com.navisa.be.chat.model.enums.ProposalStatus;
import com.navisa.be.chat.repository.ProposalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class ProposalTestFixture {

    private final ProposalRepository proposalRepository;

    public Proposal createProposal(ChatRoom chatRoom, UUID senderId, ProposalStatus status) {
        Proposal proposal = new Proposal(chatRoom, senderId);
        ReflectionTestUtils.setField(proposal, "status", status);
        return proposalRepository.save(proposal);
    }
}

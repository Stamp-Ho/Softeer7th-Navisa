package com.navisa.be.chat.repository;

import com.navisa.be.chat.model.entity.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {
}

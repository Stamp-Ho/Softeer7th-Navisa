package com.navisa.be.chat.dto.projection;

import com.navisa.be.chat.model.enums.ProposalStatus;

import java.util.UUID;

public interface ChatRoomProposalStatusProjection {
    Long getChatRoomId();
    ProposalStatus getStatus();
    UUID getSenderId();
}

package com.navisa.be.chat.dto.projection;

import com.navisa.be.chat.model.enums.ProposalStatus;

import java.util.UUID;

public record ChatRoomProposalStatusProjection(
    Long chatRoomId,
    ProposalStatus status,
    UUID senderId
) {
}

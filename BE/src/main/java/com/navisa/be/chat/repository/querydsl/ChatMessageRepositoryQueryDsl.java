package com.navisa.be.chat.repository.querydsl;

import java.util.UUID;

public interface ChatMessageRepositoryQueryDsl {
    Long findNonReadCountByProfileId(UUID profileId, boolean isForeigner);
    Long findMatchedNonReadCountByAgentId(UUID profileId);
}

package com.navisa.be.chat.repository.querydsl;

import com.navisa.be.chat.dto.projection.ChatRoomInfoProjection;
import com.navisa.be.chat.model.enums.ChatRoomFilterType;
import com.navisa.be.global.web.request.SliceRequest;

import java.util.List;
import java.util.UUID;

public interface ChatRoomQueryDsl {
    List<ChatRoomInfoProjection> findByNoOffset(UUID profileId, SliceRequest<Long> slice, boolean isForeignerId, ChatRoomFilterType filter);
}

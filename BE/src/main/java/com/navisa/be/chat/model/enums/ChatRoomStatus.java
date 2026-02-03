package com.navisa.be.chat.model.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ChatRoomStatus {
    DEFAULT("채팅방 기본 상태"),
    BLOCKED("채팅방 차단 상태");

    private final String value;
}

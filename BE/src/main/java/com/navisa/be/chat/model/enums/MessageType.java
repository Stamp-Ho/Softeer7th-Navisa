package com.navisa.be.chat.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageType {

    TEXT("기본 채팅 메시지"),
    PROPOSAL("수임 제안 메시지"),
    ACCEPTED("수임 수락 메시지"),
    REJECTED("거절 메시지"),
    CANCELED("수임 취소 메시지"),
    SYSTEM("시스템 메시지"),
    READ("읽음처리 메시지");

    private final String description;
}
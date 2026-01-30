package com.navisa.be.chat.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProposalStatus {

    DEFAULT("상태 없음 (수임 제안 전)"),
    PROPOSED("행정사의 수임 제안"),
    MATCHED("수임 수락"),
    REJECTED("단방향 수임 취소"),
    BLOCKED("채팅 차단");

    private final String description;
}
package com.navisa.be.chat.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProposalStatus {

    PROPOSED("행정사의 수임 제안"),
    MATCHED("수임 수락"),
    REJECTED("단방향 수임 취소"),
    COMPLETED("수임 완료");

    private final String description;
}
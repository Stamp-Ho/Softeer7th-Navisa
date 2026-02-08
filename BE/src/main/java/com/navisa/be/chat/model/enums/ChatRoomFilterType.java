package com.navisa.be.chat.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ChatRoomFilterType {
    ALL("all"),
    UNREAD("unread"),
    MATCHED("matched");

    private final String value;

    public static ChatRoomFilterType from(String filter) {
        // null이거나 비어있으면 기본값인 ALL 반환
        if (filter == null || filter.isBlank()) {
            return ALL;
        }

        return Arrays.stream(values())
                .filter(t -> t.value.equalsIgnoreCase(filter))
                .findFirst()
                .orElse(ALL); // 알 수 없는 값이 들어와도 ALL로 처리
    }
}
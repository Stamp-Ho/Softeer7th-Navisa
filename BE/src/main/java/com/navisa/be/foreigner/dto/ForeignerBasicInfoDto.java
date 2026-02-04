package com.navisa.be.foreigner.dto;

import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.foreigner.model.entity.ForeignerLanguage;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.user.model.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Schema(description = "기본 정보 dto")
public record ForeignerBasicInfoDto(
        @Schema(description = "외국인 id")
        UUID foreignerId,
        @Schema(description = "닉네임")
        String nickname,
        @Schema(description = "국적 id 리스트")
        List<Long> nationIdList,
        @Schema(description = "최근 접속일시")
        LocalDateTime lastAccessDay,
        @Schema(description = "행정사와 외국인 사이의 채팅방 존재 여부")
        boolean hasChatRoomBetween,
        @Schema(description = "채팅방 id")
        Long chatRoomId
) {

        public static ForeignerBasicInfoDto of(ForeignerProfile foreignerProfile, List<Long> nationIds, User user, Optional<ChatRoom> optChatRoom) {
                return new ForeignerBasicInfoDto(
                        foreignerProfile.getId(),
                        foreignerProfile.getNickname(),
                        nationIds,
                        user.getLastLoginAt(),
                        optChatRoom.isPresent(),
                        optChatRoom.map(ChatRoom::getId).orElse(null)
                );
        }
}

package com.navisa.be.chat.dto.response;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChatRoomCardResponseTest {

    @Test
    @DisplayName("Foreigner 입장일 때 DTO 변환이 올바르게 수행된다")
    void toDto_Foreigner() {
        // given
        ForeignerProfile foreignerProfile = mock(ForeignerProfile.class);
        AgentProfile agentProfile = mock(AgentProfile.class);
        ChatRoom chatRoom = mock(ChatRoom.class);

        when(chatRoom.getId()).thenReturn(1L);
        when(chatRoom.getStatus()).thenReturn(ChatRoomStatus.DEFAULT);
        when(chatRoom.getLastChattedAt()).thenReturn(ZonedDateTime.now());
        when(chatRoom.getAgentProfile()).thenReturn(agentProfile);
        when(agentProfile.getName()).thenReturn("Agent Name");

        boolean isForeigner = true;
        String profileImgUrl = "http://image.url";
        String lastMessage = "Hello World";
        Long unreadCount = 5L;

        // when
        ChatRoomCardResponse response = ChatRoomCardResponse.toDto(chatRoom, profileImgUrl, lastMessage, unreadCount,
                isForeigner);

        // then
        assertThat(response.chatRoomId()).isEqualTo(1L);
        assertThat(response.profileImgUrl()).isEqualTo(profileImgUrl);
        assertThat(response.opponentName()).isEqualTo("Agent Name"); // Foreigner는 Agent 이름을 봄
        assertThat(response.noneReadCount()).isEqualTo(unreadCount);
        assertThat(response.lastMessage()).isEqualTo(lastMessage);
    }

    @Test
    @DisplayName("Agent 입장일 때 DTO 변환이 올바르게 수행된다")
    void toDto_Agent() {
        // given
        ForeignerProfile foreignerProfile = mock(ForeignerProfile.class);
        ChatRoom chatRoom = mock(ChatRoom.class);

        when(chatRoom.getId()).thenReturn(2L);
        when(chatRoom.getStatus()).thenReturn(ChatRoomStatus.DEFAULT);
        when(chatRoom.getLastChattedAt()).thenReturn(ZonedDateTime.now());
        when(chatRoom.getForeignerProfile()).thenReturn(foreignerProfile);
        when(foreignerProfile.getNickname()).thenReturn("Foreigner Nick");

        boolean isForeigner = false;
        String profileImgUrl = "http://image.url";
        String lastMessage = "Hello Agent";
        Long unreadCount = 2L;

        // when
        ChatRoomCardResponse response = ChatRoomCardResponse.toDto(chatRoom, profileImgUrl, lastMessage, unreadCount,
                isForeigner);

        // then
        assertThat(response.chatRoomId()).isEqualTo(2L);
        assertThat(response.opponentName()).isEqualTo("Foreigner Nick"); // Agent는 Foreigner 닉네임을 봄
    }
}

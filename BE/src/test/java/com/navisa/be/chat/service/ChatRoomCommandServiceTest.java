package com.navisa.be.chat.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.chat.dto.request.CreateChatRoomRequest;
import com.navisa.be.chat.dto.response.CreateChatRoomResponse;
import com.navisa.be.chat.exception.ChatRoomException;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import com.navisa.be.chat.repository.ChatRoomRepository;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.support.*;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class ChatRoomCommandServiceTest extends IntegrationTestSupport {

    @Autowired
    private ChatRoomCommandService chatRoomCommandService;
    @Autowired
    private AgentProfileTestFixture agentProfileTestFixture;
    @Autowired
    private UserTestFixture userTestFixture;
    @Autowired
    private ForeignerProfileTestFixture foreignerProfileTestFixture;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ChatRoomTestFixture chatRoomTestFixture;


    @Test
    @DisplayName("VALID_AGENT는 FILLED_FOREIGNER와의 채팅방 생성에 성공한다")
    void create_shouldSucceed() {
        // given
        User agentuser = userTestFixture.createUser("agent", UserType.VALID_AGENT);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("박행정", "서울시", agentuser.getId());

        User foreignerUser = userTestFixture.createUser("foreigner", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        String content = "안녕";
        ZonedDateTime sentAt = ZonedDateTime.now();
        CreateChatRoomRequest request = new CreateChatRoomRequest(foreignerProfile.getId(), content, sentAt);

        // when
        CreateChatRoomResponse response = chatRoomCommandService.create(request, agentuser.getEmail());

        // then
        boolean exists = chatRoomRepository.existsByAgentProfileIdAndForeignerProfileId(agentProfile.getId(), foreignerProfile.getId());
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("INVALID_AGENT는 채팅방 생성 시에 예외가 발생한다")
    void create_shouldThrowException_whenInvalidAgent() {
        // given
        User agentuser = userTestFixture.createUser("agent", UserType.INVALID_AGENT);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("박행정", "서울시", agentuser.getId());

        User foreignerUser = userTestFixture.createUser("foreigner", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        String content = "안녕";
        ZonedDateTime sentAt = ZonedDateTime.now();
        CreateChatRoomRequest request = new CreateChatRoomRequest(foreignerProfile.getId(), content, sentAt);

        // when & then
        assertThatThrownBy(() -> chatRoomCommandService.create(request, agentuser.getEmail()))
                .isInstanceOf(ChatRoomException.class)
                .hasMessage(ResponseStatus.FORBIDDEN.getMessage());

    }

    @Test
    @DisplayName("채팅방이 이미 있으면 예외가 발생한다")
    void create_shouldThrowException_whenChatRoomExists() {
        // given
        User agentuser = userTestFixture.createUser("agent", UserType.VALID_AGENT);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("박행정", "서울시", agentuser.getId());

        User foreignerUser = userTestFixture.createUser("foreigner", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT, ZonedDateTime.now().minusDays(3));

        String content = "안녕";
        ZonedDateTime sentAt = ZonedDateTime.now();
        CreateChatRoomRequest request = new CreateChatRoomRequest(foreignerProfile.getId(), content, sentAt);

        // when & then
        assertThatThrownBy(() -> chatRoomCommandService.create(request, agentuser.getEmail()))
                .isInstanceOf(ChatRoomException.class)
                .hasMessage(ResponseStatus.CHATROOM_ALREADY_EXISTS.getMessage());
    }

}

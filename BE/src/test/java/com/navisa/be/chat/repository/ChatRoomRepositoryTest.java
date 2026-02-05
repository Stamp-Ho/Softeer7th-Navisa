package com.navisa.be.chat.repository;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import com.navisa.be.common.dto.request.SliceRequest;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.support.AgentProfileTestFixture;
import com.navisa.be.support.ChatRoomTestFixture;
import com.navisa.be.support.ForeignerProfileTestFixture;
import com.navisa.be.support.IntegrationTestSupport;
import com.navisa.be.support.UserTestFixture;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class ChatRoomRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatRoomTestFixture chatRoomTestFixture;

    @Autowired
    private ForeignerProfileTestFixture foreignerProfileTestFixture;

    @Autowired
    private AgentProfileTestFixture agentProfileTestFixture;

    @Autowired
    private UserTestFixture userTestFixture;

    @Test
    @DisplayName("AgentId와 ForeignerId로 채팅방을 조회한다")
    void findByAgentIdAndForeignerId() {
        // given
        User foreignerUser = userTestFixture.createUser("f@test.com", UserType.FILLED_FOREIGNER);
        User agentUser = userTestFixture.createUser("a@test.com", UserType.VALID_AGENT);

        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent", "Addr", agentUser.getId());

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT,
                ZonedDateTime.now());

        // when
        Optional<ChatRoom> result = chatRoomRepository.findByAgentIdAndForeignerId(agentProfile.getId(),
                foreignerProfile.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(chatRoom.getId());
    }

    @Test
    @DisplayName("NoOffset 방식으로 채팅방 목록을 조회한다 (Foreigner 기준)")
    void findByNoOffset_Foreigner() {
        // given
        User foreignerUser = userTestFixture.createUser("f@test.com", UserType.FILLED_FOREIGNER);
        User agentUser1 = userTestFixture.createUser("a1@test.com", UserType.VALID_AGENT);
        User agentUser2 = userTestFixture.createUser("a2@test.com", UserType.VALID_AGENT);

        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);
        AgentProfile agentProfile1 = agentProfileTestFixture.createAgentProfile("Agent1", "Addr", agentUser1.getId());
        AgentProfile agentProfile2 = agentProfileTestFixture.createAgentProfile("Agent2", "Addr", agentUser2.getId());

        // Room 1 (Earliest)
        ChatRoom chatRoom1 = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile1, ChatRoomStatus.DEFAULT,
                ZonedDateTime.now().minusMinutes(10));
        // Room 2 (Latest)
        ChatRoom chatRoom2 = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile2, ChatRoomStatus.DEFAULT,
                ZonedDateTime.now());

        SliceRequest<Long> sliceRequest = new SliceRequest<>(null, 10);

        // when (isForeignerId = true -> Foreigner가 자신의 채팅방 조회)
        List<ChatRoom> result = chatRoomRepository.findByNoOffset(foreignerProfile.getId(), sliceRequest, true);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(chatRoom2.getId()); // 최신순
        assertThat(result.get(1).getId()).isEqualTo(chatRoom1.getId());
    }
}

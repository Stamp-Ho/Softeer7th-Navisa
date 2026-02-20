package com.navisa.be.chat.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.chat.dto.response.ChatRoomCardResponse;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import com.navisa.be.chat.model.enums.ProposalStatus;
import com.navisa.be.global.web.request.SliceRequest;
import com.navisa.be.global.web.response.SliceResponse;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.support.*;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class ChatRoomServiceFacadeTest extends IntegrationTestSupport {

    @Autowired
    private ChatRoomServiceFacade chatRoomServiceFacade;

    @Autowired
    private ChatRoomTestFixture chatRoomTestFixture;

    @Autowired
    private ForeignerProfileTestFixture foreignerProfileTestFixture;

    @Autowired
    private AgentProfileTestFixture agentProfileTestFixture;

    @Autowired
    private UserTestFixture userTestFixture;

    @Autowired
    private ProposalTestFixture proposalTestFixture;

    @Test
    @DisplayName("Foreigner가 채팅방 목록을 조회한다 (안 읽은 메시지 수, 마지막 메시지 포함)")
    void findAllChatRoomsByNoOffset() {
        // given
        User foreignerUser = userTestFixture.createUser("f@test.com", UserType.FILLED_FOREIGNER);
        User agentUser1 = userTestFixture.createUser("a1@test.com", UserType.VALID_AGENT); // Room 1 Owner
        User agentUser2 = userTestFixture.createUser("a2@test.com", UserType.VALID_AGENT); // Room 2 Owner

        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);
        AgentProfile agentProfile1 = agentProfileTestFixture.createAgentProfile("Agent1", "Addr",
                agentUser1.getId());
        AgentProfile agentProfile2 = agentProfileTestFixture.createAgentProfile("Agent2", "Addr",
                agentUser2.getId());

        // Room 1: Agent1과 대화. 메시지 있음. 안 읽은 메시지 1개
        ChatRoom chatRoom1 = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile1,
                ChatRoomStatus.DEFAULT, LocalDateTime.now().minusMinutes(10));
        chatRoomTestFixture.createChatMessage(chatRoom1, agentProfile1.getId(), "Last Msg 1", false);

        // Room 2: Agent2와 대화. 메시지 있음. 모두 읽음. (가장 최근)
        ChatRoom chatRoom2 = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile2,
                ChatRoomStatus.DEFAULT, LocalDateTime.now().minusMinutes(5));
        chatRoomTestFixture.createChatMessage(chatRoom2, agentProfile2.getId(), "Last Msg 2", true);

        // when
        SliceRequest<Long> sliceRequest = new SliceRequest<>(null, 10);
        SliceResponse<ChatRoomCardResponse, Long> response = chatRoomServiceFacade
                .findAllChatRoomsByNoOffset(foreignerUser.getEmail(), null, sliceRequest);

        // then
        List<ChatRoomCardResponse> values = response.content();
        assertThat(values).hasSize(2);

        ChatRoomCardResponse res1 = values.get(0); // Room 2
        assertThat(res1.chatRoomId()).isEqualTo(chatRoom2.getId());
        assertThat(res1.lastMessage()).isEqualTo("Last Msg 2");
        assertThat(res1.noneReadCount()).isEqualTo(0L);
        assertThat(res1.opponentName()).isEqualTo(agentProfile2.getName());
        assertThat(res1.proposed()).isFalse();
        assertThat(res1.proposalMatched()).isFalse();

        ChatRoomCardResponse res2 = values.get(1); // Room 1
        assertThat(res2.chatRoomId()).isEqualTo(chatRoom1.getId());
        assertThat(res2.lastMessage()).isEqualTo("Last Msg 1");
        assertThat(res2.noneReadCount()).isEqualTo(1L);
        assertThat(res2.opponentName()).isEqualTo(agentProfile1.getName());
        assertThat(res2.proposed()).isFalse();
        assertThat(res2.proposalMatched()).isFalse();
    }

    @Test
    @DisplayName("외국인은 읽지 않은 채팅방 목록 조회에 성공한다")
    void findAllChatRoomsByNoOffset_returnUnreadChatRoom_whenFilterIsUnread() {
        // given
        User foreignerUser = userTestFixture.createUser("f@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        // Agent1과 대화. 메시지 있음. 안 읽은 메시지 1개
        User agentUser1 = userTestFixture.createUser("a1@test.com", UserType.VALID_AGENT);
        AgentProfile agentProfile1 = agentProfileTestFixture.createAgentProfile("Agent1", "Addr",
                agentUser1.getId());

        ChatRoom chatRoom1 = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile1,
                ChatRoomStatus.DEFAULT, LocalDateTime.now().minusMinutes(10));
        chatRoomTestFixture.createChatMessage(chatRoom1, agentProfile1.getId(), "Last Msg 1", false);

        // Agent2과 대화. 메시지 있음. 안 읽은 메시지 1개
        User agentUser2 = userTestFixture.createUser("a2@test.com", UserType.VALID_AGENT);
        AgentProfile agentProfile2 = agentProfileTestFixture.createAgentProfile("Agent2", "Addr",
                agentUser2.getId());

        ChatRoom chatRoom2 = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile2,
                ChatRoomStatus.DEFAULT, LocalDateTime.now().minusMinutes(5));

        chatRoomTestFixture.createChatMessage(chatRoom2, agentProfile2.getId(), "Last Msg 2", false);
        chatRoomTestFixture.createChatMessage(chatRoom2, agentProfile2.getId(), "Last Msg 2-2", false);

        proposalTestFixture.createProposal(chatRoom2, agentProfile2.getId(), ProposalStatus.PROPOSED);

        // Agent3와 대화. 메시지 있음. 모두 읽음
        User agentUser3 = userTestFixture.createUser("a3@test.com", UserType.VALID_AGENT);
        AgentProfile agentProfile3 = agentProfileTestFixture.createAgentProfile("Agent3", "Addr",
                agentUser3.getId());

        ChatRoom chatRoom3 = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile3,
                ChatRoomStatus.DEFAULT, LocalDateTime.now());
        chatRoomTestFixture.createChatMessage(chatRoom3, agentProfile3.getId(), "Last Msg 3", true);

        // when
        SliceRequest<Long> sliceRequest = new SliceRequest<>(null, 10);
        SliceResponse<ChatRoomCardResponse, Long> response = chatRoomServiceFacade
                .findAllChatRoomsByNoOffset(foreignerUser.getEmail(), "unread", sliceRequest);

        // then
        List<ChatRoomCardResponse> values = response.content();
        assertThat(values).hasSize(2);

        // Room 1
        assertThat(values.get(0).chatRoomId()).isEqualTo(chatRoom2.getId());
        assertThat(values.get(0).noneReadCount()).isEqualTo(2L);
        assertThat(values.get(0).lastMessage()).isEqualTo("Last Msg 2-2");
        assertThat(values.get(0).opponentName()).isEqualTo(agentProfile2.getName());
        assertThat(values.get(0).proposed()).isTrue();
        assertThat(values.get(0).proposalMatched()).isFalse();

        // Room 2
        assertThat(values.get(1).chatRoomId()).isEqualTo(chatRoom1.getId());
        assertThat(values.get(1).noneReadCount()).isEqualTo(1L);
        assertThat(values.get(1).lastMessage()).isEqualTo("Last Msg 1");
        assertThat(values.get(1).opponentName()).isEqualTo(agentProfile1.getName());
        assertThat(values.get(1).proposed()).isFalse();
        assertThat(values.get(1).proposalMatched()).isFalse();
    }

    @Test
    @DisplayName("행정사는 수임중인 채팅방 목록 조회에 성공한다")
    void findAllChatRoomsByNoOffset_returnMatchedChatRoom_whenFilterIsMatched() {
        // given
        User agentUser = userTestFixture.createUser("a1@test.com", UserType.VALID_AGENT);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent", "Addr",
                agentUser.getId());

        // 외국인1과의 대화. 메시지 있음. 안 읽은 메시지 1개. 그리고 수임중 상태.
        User foreignerUser1 = userTestFixture.createUser("f1@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile1 = foreignerProfileTestFixture.createForeignerProfile(foreignerUser1);

        ChatRoom chatRoom1 = chatRoomTestFixture.createChatRoom(foreignerProfile1, agentProfile,
                ChatRoomStatus.DEFAULT, LocalDateTime.now().minusMinutes(10));
        chatRoomTestFixture.createChatMessage(chatRoom1, foreignerProfile1.getId(), "Last Msg 1", false);
        proposalTestFixture.createProposal(chatRoom1, agentProfile.getId(), ProposalStatus.MATCHED);

        // 외국인2과 대화. 메시지 있음. 안 읽은 메시지 1개
        User foreignerUser2 = userTestFixture.createUser("f2@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile2 = foreignerProfileTestFixture.createForeignerProfile(foreignerUser2);

        ChatRoom chatRoom2 = chatRoomTestFixture.createChatRoom(foreignerProfile2, agentProfile,
                ChatRoomStatus.DEFAULT, LocalDateTime.now().minusMinutes(5));
        chatRoomTestFixture.createChatMessage(chatRoom2, foreignerProfile2.getId(), "Last Msg 2", false);
        chatRoomTestFixture.createChatMessage(chatRoom2, foreignerProfile2.getId(), "Last Msg 2-2", false);

        // 외국인3과 대화. 메시지 있음. 모두 읽음. 그리고 수임중 상태.
        User foreignerUser3 = userTestFixture.createUser("f3@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile3 = foreignerProfileTestFixture.createForeignerProfile(foreignerUser3);

        ChatRoom chatRoom3 = chatRoomTestFixture.createChatRoom(foreignerProfile3, agentProfile,
                ChatRoomStatus.DEFAULT, LocalDateTime.now());
        chatRoomTestFixture.createChatMessage(chatRoom3, foreignerProfile3.getId(), "Last Msg 3", true);
        proposalTestFixture.createProposal(chatRoom3, agentProfile.getId(), ProposalStatus.MATCHED);

        // when
        SliceRequest<Long> sliceRequest = new SliceRequest<>(null, 10);
        SliceResponse<ChatRoomCardResponse, Long> response = chatRoomServiceFacade
                .findAllChatRoomsByNoOffset(agentUser.getEmail(), "matched", sliceRequest);

        // then
        List<ChatRoomCardResponse> values = response.content();
        assertThat(values).hasSize(2);

        assertThat(values.get(0).chatRoomId()).isEqualTo(chatRoom3.getId());
        assertThat(values.get(0).noneReadCount()).isEqualTo(0L);
        assertThat(values.get(0).lastMessage()).isEqualTo("Last Msg 3");
        assertThat(values.get(0).opponentName()).isEqualTo(foreignerProfile3.getNickname());
        assertThat(values.get(0).proposed()).isFalse(); // 수임 상태 확인
        assertThat(values.get(0).proposalMatched()).isTrue();

        assertThat(values.get(1).chatRoomId()).isEqualTo(chatRoom1.getId());
        assertThat(values.get(1).noneReadCount()).isEqualTo(1L);
        assertThat(values.get(1).lastMessage()).isEqualTo("Last Msg 1");
        assertThat(values.get(1).opponentName()).isEqualTo(foreignerProfile1.getNickname());
        assertThat(values.get(1).proposed()).isFalse(); // 수임 상태 확인
        assertThat(values.get(1).proposalMatched()).isTrue();
    }
}

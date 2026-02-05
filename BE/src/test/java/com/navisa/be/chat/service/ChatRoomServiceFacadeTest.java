package com.navisa.be.chat.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.chat.dto.response.ChatRoomCardResponse;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import com.navisa.be.common.dto.request.SliceRequest;
import com.navisa.be.common.dto.response.SliceResponse;
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
                                ChatRoomStatus.DEFAULT,
                                ZonedDateTime.now().minusMinutes(5));
                chatRoomTestFixture.createChatMessage(chatRoom1, agentProfile1.getId(), "Last Msg 1", false);

                // Room 2: Agent2와 대화. 메시지 있음. 모두 읽음. (가장 최근)
                ChatRoom chatRoom2 = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile2,
                                ChatRoomStatus.DEFAULT,
                                ZonedDateTime.now());
                chatRoomTestFixture.createChatMessage(chatRoom2, agentProfile2.getId(), "Last Msg 2", true);

                // when
                SliceRequest<Long> sliceRequest = new SliceRequest<>(null, 10);
                SliceResponse<ChatRoomCardResponse, Long> response = chatRoomServiceFacade
                                .findAllChatRoomsByNoOffset(foreignerUser.getEmail(), sliceRequest);

                // then
                List<ChatRoomCardResponse> values = response.content();
                assertThat(values).hasSize(2);

                ChatRoomCardResponse res1 = values.get(0); // Room 2
                assertThat(res1.chatRoomId()).isEqualTo(chatRoom2.getId());
                assertThat(res1.lastMessage()).isEqualTo("Last Msg 2");
                assertThat(res1.noneReadCount()).isEqualTo(0L);
                assertThat(res1.opponentName()).isEqualTo(agentProfile2.getName());

                ChatRoomCardResponse res2 = values.get(1); // Room 1
                assertThat(res2.chatRoomId()).isEqualTo(chatRoom1.getId());
                assertThat(res2.lastMessage()).isEqualTo("Last Msg 1");
                assertThat(res2.noneReadCount()).isEqualTo(1L);
                assertThat(res2.opponentName()).isEqualTo(agentProfile1.getName());
        }
}

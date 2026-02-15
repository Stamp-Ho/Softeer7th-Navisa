package com.navisa.be.chat.repository;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.chat.dto.projection.ChatMessageNonReadCountProjection;
import com.navisa.be.chat.model.entity.ChatMessage;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import com.navisa.be.global.web.request.SliceRequest;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.support.AgentProfileTestFixture;
import com.navisa.be.support.ChatRoomTestFixture;
import com.navisa.be.support.ForeignerProfileTestFixture;
import com.navisa.be.support.IntegrationTestSupport;
import com.navisa.be.support.UserTestFixture;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.chat.model.entity.Proposal;
import com.navisa.be.chat.model.enums.ProposalStatus;
import com.navisa.be.user.model.enums.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class ChatMessageRepositoryTest extends IntegrationTestSupport {

        @Autowired
        private ChatMessageRepository chatMessageRepository;

        @Autowired
        private ChatRoomTestFixture chatRoomTestFixture;

        @Autowired
        private ForeignerProfileTestFixture foreignerProfileTestFixture;

        @Autowired
        private AgentProfileTestFixture agentProfileTestFixture;

        @Autowired
        private UserTestFixture userTestFixture;

        @Autowired
        private ProposalRepository proposalRepository;

        @Test
        @DisplayName("채팅방 목록에서 각 채팅방의 마지막 메시지를 조회한다")
        void findAllLastChatMessageGroupByChatRoom() {
                // given
                User agentUser = userTestFixture.createUser("agent@test.com", UserType.VALID_AGENT);
                AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent", "Address",
                        agentUser.getId());

                User foreignerUser1 = userTestFixture.createUser("foreigner1@test.com", UserType.FILLED_FOREIGNER);
                ForeignerProfile foreignerProfile1 = foreignerProfileTestFixture.createForeignerProfile(foreignerUser1);

                User foreignerUser2 = userTestFixture.createUser("foreigner2@test.com", UserType.FILLED_FOREIGNER);
                ForeignerProfile foreignerProfile2 = foreignerProfileTestFixture.createForeignerProfile(foreignerUser2);

                ChatRoom chatRoom1 = chatRoomTestFixture.createChatRoom(foreignerProfile1, agentProfile,
                                ChatRoomStatus.DEFAULT,
                                ZonedDateTime.now());
                ChatRoom chatRoom2 = chatRoomTestFixture.createChatRoom(foreignerProfile2, agentProfile,
                                ChatRoomStatus.DEFAULT,
                                ZonedDateTime.now());

                chatRoomTestFixture.createChatMessage(chatRoom1, foreignerProfile1.getId(), "Room1 Msg1", true);
                ChatMessage lastMsg1 = chatRoomTestFixture.createChatMessage(chatRoom1, agentProfile.getId(),
                                "Room1 Msg2",
                                true);

                chatRoomTestFixture.createChatMessage(chatRoom2, foreignerProfile2.getId(), "Room2 Msg1", true);
                ChatMessage lastMsg2 = chatRoomTestFixture.createChatMessage(chatRoom2, agentProfile.getId(),
                                "Room2 Msg2",
                                true);

                // when
                List<ChatMessage> lastMessages = chatMessageRepository
                                .findAllLastChatMessageGroupByChatRoom(List.of(chatRoom1.getId(), chatRoom2.getId()));

                // then
                assertThat(lastMessages).hasSize(2)
                                .extracting("id")
                                .containsExactlyInAnyOrder(lastMsg1.getId(), lastMsg2.getId());
        }

        @Test
        @DisplayName("채팅방 목록에서 각 채팅방의 안 읽은 메시지 수를 조회한다")
        void findCountByChatRoomIn() {
                // given
                User agentUser1 = userTestFixture.createUser("agent1@test.com", UserType.VALID_AGENT);
                AgentProfile agentProfile1 = agentProfileTestFixture.createAgentProfile("Agent", "Address",
                        agentUser1.getId());

                User agentUser2 = userTestFixture.createUser("agent2@test.com", UserType.VALID_AGENT);
                AgentProfile agentProfile2 = agentProfileTestFixture.createAgentProfile("Agent", "Address",
                        agentUser2.getId());

                User foreignerUser = userTestFixture.createUser("foreigner@test.com", UserType.FILLED_FOREIGNER);
                ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

                ChatRoom chatRoom1 = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile1,
                                ChatRoomStatus.DEFAULT,
                                ZonedDateTime.now());
                ChatRoom chatRoom2 = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile2,
                                ChatRoomStatus.DEFAULT,
                                ZonedDateTime.now());

                // Room 1: Agent가 보낸 메시지 2개 (안 읽음), Foreigner가 보낸 메시지 1개
                chatRoomTestFixture.createChatMessage(chatRoom1, agentProfile1.getId(), "Msg1", false);
                chatRoomTestFixture.createChatMessage(chatRoom1, agentProfile1.getId(), "Msg2", false);
                chatRoomTestFixture.createChatMessage(chatRoom1, foreignerProfile.getId(), "My Msg", false);

                // Room 2: Agent가 보낸 메시지 1개 (읽음)
                chatRoomTestFixture.createChatMessage(chatRoom2, agentProfile2.getId(), "Msg3", true);

                // when - Foreigner 입장에서 조회 (상대방이 보낸 안 읽은 메시지)
                List<ChatMessageNonReadCountProjection> result = chatMessageRepository.findCountByChatRoomIn(
                                List.of(chatRoom1, chatRoom2), foreignerProfile.getId());

                // then
                assertThat(result).hasSize(1);

                ChatMessageNonReadCountProjection countProj = result.stream()
                                .filter(p -> p.getId().equals(chatRoom1.getId()))
                                .findFirst()
                                .orElseThrow();

                assertThat(countProj.getCount()).isEqualTo(2L);
        }

        @Test
        @DisplayName("profileId로 전체 안 읽은 메시지 수를 조회한다")
        void findNonReadCountByProfileId() {
                // given
                User foreignerUser = userTestFixture.createUser("foreigner@test.com", UserType.FILLED_FOREIGNER);
                User agentUser = userTestFixture.createUser("agent@test.com", UserType.VALID_AGENT);

                ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);
                AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent", "Address",
                                agentUser.getId());

                ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile,
                                ChatRoomStatus.DEFAULT,
                                ZonedDateTime.now());

                // Agent -> Foreigner (안 읽음) 2개
                chatRoomTestFixture.createChatMessage(chatRoom, agentProfile.getId(), "Msg1", false);
                chatRoomTestFixture.createChatMessage(chatRoom, agentProfile.getId(), "Msg2", false);

                // Foreigner -> Agent (안 읽음) 1개 (Foreigner 입장에서는 내가 보낸 거라 카운트 X)
                chatRoomTestFixture.createChatMessage(chatRoom, foreignerProfile.getId(), "My Msg", false);

                // when
                Long count = chatMessageRepository.findNonReadCountByProfileId(foreignerProfile.getId(), true);

                // then
                assertThat(count).isEqualTo(2L);
        }

        @Test
        @DisplayName("Agent ID로 매칭된(대화 내역이 있는) 채팅방 수를 조회한다 (QueryDSL)")
        void findMatchedCountByAgentId() {
                // given
                User foreignerUser1 = userTestFixture.createUser("f1@test.com", UserType.FILLED_FOREIGNER);
                User foreignerUser2 = userTestFixture.createUser("f2@test.com", UserType.FILLED_FOREIGNER);
                User agentUser = userTestFixture.createUser("agent@test.com", UserType.VALID_AGENT);

                ForeignerProfile foreignerProfile1 = foreignerProfileTestFixture.createForeignerProfile(foreignerUser1);
                ForeignerProfile foreignerProfile2 = foreignerProfileTestFixture.createForeignerProfile(foreignerUser2);
                AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent", "Address",
                                agentUser.getId());

                // Room 1: 메시지 있음 (매칭됨)
                ChatRoom chatRoom1 = chatRoomTestFixture.createChatRoom(foreignerProfile1, agentProfile,
                                ChatRoomStatus.DEFAULT,
                                ZonedDateTime.now());
                Proposal proposal1 = new Proposal(chatRoom1, agentProfile.getId());
                ReflectionTestUtils.setField(proposal1, "status", ProposalStatus.MATCHED);
                proposalRepository.save(proposal1);

                chatRoomTestFixture.createChatMessage(chatRoom1, foreignerProfile1.getId(), "Hello", false);

                // Room 2: 메시지 없음 (매칭 안됨)
                ChatRoom chatRoom2 = chatRoomTestFixture.createChatRoom(foreignerProfile2, agentProfile,
                                ChatRoomStatus.DEFAULT,
                                ZonedDateTime.now());

                // when
                Long count = chatMessageRepository.findMatchedNonReadCountByAgentId(agentProfile.getId());

                // then
                assertThat(count).isEqualTo(1L);
        }

        @Test
        @DisplayName("채팅방 메시지를 No-Offset 방식으로 조회한다 (다른 채팅방 메시지 제외 확인)")
        void findChatMessagesByChatRoomIdAndNoOffset() {
                // given
                User foreignerUser = userTestFixture.createUser("foreigner@test.com", UserType.FILLED_FOREIGNER);
                User agentUser1 = userTestFixture.createUser("agent1@test.com", UserType.VALID_AGENT);
                User agentUser2 = userTestFixture.createUser("agent2@test.com", UserType.VALID_AGENT);

                ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);
                AgentProfile agentProfile1 = agentProfileTestFixture.createAgentProfile("Agent", "Address", agentUser1.getId());
                AgentProfile agentProfile2 = agentProfileTestFixture.createAgentProfile("Agent", "Address", agentUser2.getId());

                // LocalDateTime으로 시간 기준 설정 (BaseEntity 타입과 일치)
                LocalDateTime now = LocalDateTime.now();

                ChatRoom targetRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile1, ChatRoomStatus.DEFAULT, ZonedDateTime.now());
                ChatRoom otherRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile2, ChatRoomStatus.DEFAULT, ZonedDateTime.now());

                // 타겟 채팅방 메시지 30개 생성
                for (int i = 1; i <= 30; i++) {
                        ChatMessage message = chatRoomTestFixture.createChatMessage(targetRoom, foreignerProfile.getId(), "Target " + i, false);
                        ReflectionTestUtils.setField(message, "createdAt", now.plusMinutes(i));
                        chatMessageRepository.save(message);
                }

                // 소음용 메시지 10개 생성
                for (int i = 1; i <= 10; i++) {
                        ChatMessage otherMessage = chatRoomTestFixture.createChatMessage(otherRoom, foreignerProfile.getId(), "Other " + i, false);
                        ReflectionTestUtils.setField(otherMessage, "createdAt", now.plusMinutes(i));
                        chatMessageRepository.save(otherMessage);
                }

                // when - 1페이지 조회
                SliceRequest<Long> sliceRequest1 = new SliceRequest<>(null, 10);
                List<ChatMessage> page1 = chatMessageRepository.findChatMessagesByChatRoomIdAndNoOffset(targetRoom.getId(), sliceRequest1);

                // then - 검증
                assertThat(page1).hasSize(11);
                assertThat(page1.get(0).getContent()).isEqualTo("Target 30");
                assertThat(page1.get(9).getContent()).isEqualTo("Target 21");
                assertThat(page1).extracting("chatRoom.id").containsOnly(targetRoom.getId());

                // when - 2페이지 조회
                Long lastId = page1.get(9).getId();
                SliceRequest<Long> sliceRequest2 = new SliceRequest<>(lastId, 10);
                List<ChatMessage> page2 = chatMessageRepository.findChatMessagesByChatRoomIdAndNoOffset(targetRoom.getId(), sliceRequest2);

                // then - 검증
                assertThat(page2).hasSize(11);
                assertThat(page2.get(0).getContent()).isEqualTo("Target 20");
                assertThat(page2.get(9).getContent()).isEqualTo("Target 11");
        }
}

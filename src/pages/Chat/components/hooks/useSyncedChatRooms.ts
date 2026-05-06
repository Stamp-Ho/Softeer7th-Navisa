import { useEffect, useMemo } from "react";
import { deriveStatusFromMessageType } from "../utils/getChatStatus";
import { determineRoomStatus } from "../utils/determineRoomStatus";
import type { Message } from "../../../../api/websocket/types";
import type { ChatRoomResponse } from "../../../../api/types/chat";
import { useQueryClient } from "@tanstack/react-query";

// 수임 상태 관련 메시지 타입들
// 이 타입의 메시지가 오면 roomStatus를 변경
const PROPOSAL_TYPES = new Set([
  "ACCEPTED",
  "CANCELED",
  "PROPOSAL",
  "REJECTED",
]);
const NO_ROOM_SELECTED = -1;

export const useSyncedChatRooms = ({
  chatRooms, // 서버에서 받아온 채팅방 목록
  socketMessages, // 웹소켓으로 받은 메시지들 (이벤트성 데이터)
  selectedChatRoomId, // 현재 사용자가 보고 있는 방
  userId, // 현재 로그인한 사용자 id
  selectedTab, // 현재 탭 (all / unread / matched)
}: {
  chatRooms: ChatRoomResponse[];
  socketMessages: Message[];
  selectedChatRoomId: number;
  userId: string | null;
  selectedTab: "all" | "unread" | "matched";
}) => {
  // react-query 캐시 무효화를 위한 client
  // 소켓 메시지 오면 서버 데이터 다시 불러오기 위함
  const queryClient = useQueryClient();

  //소켓 메시지를 기반으로 "UI용 실시간 데이터" 생성
  // - 마지막 메시지 텍스트 갱신
  // - 마지막 채팅 시간 갱신
  // - 수임 상태 메시지 감지
  const roomRealtimeMap = useMemo(() => {
    const map = new Map<
      number,
      {
        lastMessage: string;
        lastChattedAt: string;
        hasNewMessage: boolean; // 현재는 UI 표시용 (뱃지 등 확장 대비)
        latestProposalType?: string;
        latestProposalAt?: string;
      }
    >();

    // socketMessages를 전부 돌면서 "방별 최신 상태"를 계산
    const EXCLUDE_FROM_LAST_MESSAGE = new Set([
      "READ",
      "SYSTEM",
      "REVIEW_REQUIRED",
    ]);

    for (const msg of socketMessages) {
      const roomId = Number(msg.roomId);

      // 처음 등장하는 방이면 초기값 세팅
      if (!map.has(roomId)) {
        map.set(roomId, {
          lastMessage: EXCLUDE_FROM_LAST_MESSAGE.has(msg.type)
            ? ""
            : msg.content,
          lastChattedAt: msg.createdAt,
          hasNewMessage: false,
        });
      }

      const roomData = map.get(roomId)!;

      // 마지막 메시지 갱신
      // 더 최신 createdAt이면 덮어쓰기
      if (
        msg.createdAt > roomData.lastChattedAt &&
        !EXCLUDE_FROM_LAST_MESSAGE.has(msg.type)
      ) {
        roomData.lastMessage = msg.content;
        roomData.lastChattedAt = msg.createdAt;
      }

      // 상대방 메시지 & 현재 보고 있는 방이 아닐 경우
      // UI에서 "새 메시지 있음" 표시용
      // unreadCount는 여기서 계산하지 않음
      if (msg.senderId !== userId && roomId !== selectedChatRoomId) {
        roomData.hasNewMessage = true;
      }

      // 수임 관련 메시지면 상태 저장
      if (
        PROPOSAL_TYPES.has(msg.type) &&
        (!roomData.latestProposalAt ||
          msg.createdAt > roomData.latestProposalAt)
      ) {
        roomData.latestProposalType = msg.type;
        roomData.latestProposalAt = msg.createdAt;
      }
    }

    return map;
  }, [socketMessages, selectedChatRoomId, userId]);

  // 서버 채팅방 목록 + 실시간 데이터 병합
  // 서버 데이터를 기본으로 사용
  // 소켓에서 온 "UI 변화 요소"만 덮어씀
  const syncedChatRooms = useMemo(() => {
    const updated = chatRooms.map((room) => {
      const realtime = roomRealtimeMap.get(room.chatRoomId);

      // 해당 방에 대한 소켓 이벤트가 없다면 기본값으로 사용
      const initialRoomStatus = determineRoomStatus(
        room.roomStatus,
        room.proposed,
        room.proposalMatched,
      );

      // 소켓 메시지가 있으면 가장 최신의 제안 관련 메시지로 상태 업데이트
      // 없으면 초기 상태 유지
      const finalRoomStatus = realtime?.latestProposalType
        ? deriveStatusFromMessageType(
            realtime.latestProposalType,
            initialRoomStatus,
          )
        : initialRoomStatus;

      return {
        ...room,
        lastMessage: realtime?.lastMessage || room.lastMessage, // 마지막 메시지
        lastChattedAt: realtime?.lastChattedAt ?? room.lastChattedAt, // 마지막 메시지 시간
        roomStatus: finalRoomStatus,
        // 안읽음 카운트는 서버기준
        noneReadCount:
          selectedChatRoomId === room.chatRoomId ? 0 : room.noneReadCount,
      };
    });

    // 최신 메시지 순으로 정렬
    return updated.sort(
      (a, b) =>
        new Date(b.lastChattedAt).getTime() -
        new Date(a.lastChattedAt).getTime(),
    );
  }, [chatRooms, roomRealtimeMap, selectedChatRoomId]);

  // 소켓 메시지 수신 시 서버 데이터 재요청
  useEffect(() => {
    const lastMsg = socketMessages.at(-1);
    if (!lastMsg) return;

    // 수임 관련 메시지(내가 보낸 것 포함) 또는 상대방 메시지면 refetch
    const PROPOSAL_MESSAGE_TYPES = new Set([
      "PROPOSAL",
      "ACCEPTED",
      "REJECTED",
      "CANCELED",
    ]);
    const isProposalRelated = PROPOSAL_MESSAGE_TYPES.has(lastMsg.type);
    const isFromOther = lastMsg.senderId !== userId;

    if (isProposalRelated || isFromOther) {
      queryClient.refetchQueries({
        queryKey: ["chatRooms", selectedTab],
      });
      queryClient.refetchQueries({ queryKey: ["chatUnreadCount"] });
      queryClient.refetchQueries({ queryKey: ["chatMatchedUnreadCount"] });
    }
  }, [socketMessages, selectedTab, userId, queryClient]);

  // 채팅방 입장시 서버 데이터 재요청
  useEffect(() => {
    if (selectedChatRoomId === NO_ROOM_SELECTED) return;

    queryClient.refetchQueries({
      queryKey: ["chatRooms", selectedTab],
    });
    queryClient.refetchQueries({ queryKey: ["chatUnreadCount"] });
    queryClient.refetchQueries({ queryKey: ["chatMatchedUnreadCount"] });
  }, [selectedChatRoomId, selectedTab, queryClient]);

  return {
    syncedChatRooms, // 실시간 반영된 채팅방 목록
  };
};

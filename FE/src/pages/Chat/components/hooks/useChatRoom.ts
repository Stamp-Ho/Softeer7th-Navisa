import { useEffect, useMemo, useRef } from "react";
import { v4 as uuidv4 } from "uuid";
import { useAuth } from "../../../../contexts/AuthContextProvider";
import { useWebSocket } from "../../../../contexts/WebSocketContext";
import { useChatHistoryQuery } from "../../../../api/queries/useChatHistoryQuery";
import type { ChatHistoryResponse } from "../../../../api/types/chat";
import type { Message } from "../../../../api/websocket/types";
import groupChatLogs from "../../../../utils/GroupChatLogs";

export type ChatRoomStatus =
  | "DEFAULT"
  | "PROPOSED"
  | "MATCHED"
  | "REJECTED"
  | "CANCELED"
  | "BLOCKED";

export const useChatRoom = (
  chatRoomId: number,
  isRoomActive: boolean = false,
) => {
  const scrollRef = useRef<HTMLDivElement>(null);
  const lastReadSentRef = useRef<number | null>(null);

  const { userId } = useAuth();
  const { sendMessage, isConnected, messages: socketMessages } = useWebSocket();
  const {
    data: historyData,
    isLoading,
    isError,
  } = useChatHistoryQuery(chatRoomId);

  // 1. 현재 방의 소켓 메시지만 필터링
  const roomSocketMessages = useMemo(
    () =>
      socketMessages.filter((m: Message) => Number(m.roomId) === chatRoomId),
    [socketMessages, chatRoomId],
  );

  // 2. 메시지 병합 및 정렬 (History + Socket)
  const allMessages = useMemo(() => {
    if (!historyData) return [];

    const realtimeAsHistory: ChatHistoryResponse[] = roomSocketMessages.map(
      (m) => ({
        chatMessageId: m.messageId,
        isSentByMe: m.senderId === userId,
        type: m.type,
        content: m.content,
        sentAt: m.sentAt,
        createdAt: m.createdAt,
        isRead: m.isRead,
      }),
    );

    // 중복 제거 및 정렬
    const merged = [...historyData, ...realtimeAsHistory]
      .reduce<ChatHistoryResponse[]>((acc, cur) => {
        if (!acc.some((m) => m.chatMessageId === cur.chatMessageId)) {
          acc.push(cur);
        }
        return acc;
      }, [])
      .sort(
        (a, b) =>
          new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime(),
      );

    return merged;
  }, [historyData, roomSocketMessages, userId]);

  // 3. 메시지 그룹화
  const groupedChats = useMemo(() => groupChatLogs(allMessages), [allMessages]);

  // 4. "수임 제안" 버튼 활성화 여부 계산 logic
  const pendingProposalId = useMemo(() => {
    const latestProposal = [...allMessages]
      .reverse()
      .find((m) => m.type === "PROPOSAL");
    if (!latestProposal) return null;

    const hasReplied = allMessages.some(
      (m) =>
        (m.type === "ACCEPTED" || m.type === "REJECTED") &&
        new Date(m.createdAt) > new Date(latestProposal.createdAt),
    );

    return !hasReplied ? latestProposal.chatMessageId : null;
  }, [allMessages]);

  // 5. 읽음 처리 (READ) 이펙트 통합
  useEffect(() => {
    if (!isRoomActive) return;

    if (!isConnected || !userId || allMessages.length === 0) return;

    const lastMessage = allMessages[allMessages.length - 1];
    if (!lastMessage?.chatMessageId) return;

    // 내가 보낸 건 읽음 처리 안 함
    if (lastMessage.isSentByMe) return;

    // 중복 전송 방지
    if (lastReadSentRef.current === lastMessage.chatMessageId) return;
    lastReadSentRef.current = lastMessage.chatMessageId;

    sendMessage({
      roomId: chatRoomId,
      clientMessageId: uuidv4(),
      senderId: userId,
      content: String(lastMessage.chatMessageId),
      type: "READ",
      sentAt: new Date().toISOString(),
    });
  }, [allMessages, isConnected, userId, chatRoomId, sendMessage, isRoomActive]);

  // 6. 채팅방 수임 상태 실시간 반영
  const chatStatus: ChatRoomStatus = useMemo(() => {
    if (!allMessages || allMessages.length === 0) return "DEFAULT";

    // 시스템 메시지 타입들만 추적하기 위해 역순으로 탐색
    // (가장 최신 메시지가 현재 상태를 결정하므로)
    const lastSystemMsg = [...allMessages]
      .reverse()
      .find((m) =>
        ["PROPOSAL", "ACCEPTED", "REJECTED", "CANCELED"].includes(m.type),
      );

    if (!lastSystemMsg) return "DEFAULT";

    switch (lastSystemMsg.type) {
      case "PROPOSAL":
        return "PROPOSED";
      case "ACCEPTED":
        return "MATCHED";
      case "REJECTED":
        return "DEFAULT";
      case "CANCELED":
        return "DEFAULT";
      default:
        return "DEFAULT";
    }
  }, [allMessages]);

  // 7. 자동 스크롤
  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
    }
  }, [allMessages]);

  return {
    groupedChats,
    chatStatus,
    isLoading,
    isError,
    scrollRef,
    pendingProposalId, // UI에서 어떤 메시지에 버튼을 띄울지 결정하는 ID
  };
};

import { useEffect, useMemo, useRef } from "react";
import { v4 as uuidv4 } from "uuid";
import { useAuth } from "../../../../contexts/AuthContextProvider";
import { useWebSocket } from "../../../../contexts/WebSocketContext";
import { useChatHistoryQuery } from "../../../../api/queries/useChatHistoryQuery";
import type { ChatHistoryResponse } from "../../../../api/types/chat";
import type { Message } from "../../../../api/websocket/types";
import groupChatLogs from "../../../../utils/GroupChatLogs";
import { useChatScroll } from "./useChatScroll";

export type ChatRoomStatus =
  | "DEFAULT"
  | "PROPOSED"
  | "MATCHED"
  | "REJECTED"
  | "CANCELED"
  | "CHATROOM_BLOCKED";

export const useChatRoom = (
  chatRoomId: number,
  isRoomActive: boolean = false,
  initialRoomStatus?: string,
) => {
  const lastReadSentRef = useRef<number | null>(null);

  const { userId } = useAuth();
  const { sendMessage, isConnected, messages: socketMessages } = useWebSocket();
  const {
    data: historyData,
    isLoading,
    isError,
    fetchNextPage,
    isFetchingNextPage,
    hasNextPage,
  } = useChatHistoryQuery(chatRoomId, isRoomActive);

  // 1. 현재 방의 소켓 메시지만 필터링
  const roomSocketMessages = useMemo(
    () =>
      socketMessages.filter((m: Message) => Number(m.roomId) === chatRoomId),
    [socketMessages, chatRoomId],
  );

  const historyMessages: ChatHistoryResponse[] = useMemo(() => {
    if (!historyData) return [];
    return historyData.pages.flatMap((page) => page.content);
  }, [historyData]);
  // 2. 메시지 병합 및 정렬 (History + Socket)
  const allMessages = useMemo(() => {
    if (!historyData) return [];
    const realtimeAsHistory: ChatHistoryResponse[] = roomSocketMessages.map(
      (m) => ({
        chatMessageId: m.messageId,
        isSentByMe: m.senderId === userId,
        type: m.type,
        content: m.content,
        createdAt: m.createdAt,
        isRead: m.isRead,
      }),
    );

    // 중복 제거 및 정렬
    const merged = [...historyMessages, ...realtimeAsHistory]
      .reduce<ChatHistoryResponse[]>((acc, cur) => {
        if (!acc.some((m) => m.chatMessageId === cur.chatMessageId)) {
          acc.push(cur);
        }
        return acc;
      }, [])
      .sort(
        (a, b) =>
          new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime() ||
          a.chatMessageId - b.chatMessageId, // 동일한 createdAt일 경우 ID로 정렬하여 순서 보장
      );

    return merged;
  }, [historyMessages, roomSocketMessages, userId]);

  // 3. 메시지 그룹화
  const groupedChats = useMemo(() => groupChatLogs(allMessages), [allMessages]);

  const { scrollRef, handleScroll, isAtBottom } = useChatScroll({
    chatData: allMessages,
    hasNextPage,
    isFetchingNextPage,
    fetchNextPage,
  });

  const lastSystemMessage = useMemo(() => {
    for (let i = allMessages.length - 1; i >= 0; i--) {
      const m = allMessages[i];
      if (
        [
          "PROPOSAL",
          "ACCEPTED",
          "REJECTED",
          "CANCELED",
          "CHATROOM_BLOCKED",
        ].includes(m.type)
      ) {
        return m;
      }
    }
    return null;
  }, [allMessages]);

  // 4. "수임 제안" 버튼 활성화 여부 계산
  const pendingProposalId = useMemo(() => {
    // 가장 최신 시스템 메시지가 PROPOSAL이 아닐 경우
    if (!lastSystemMessage || lastSystemMessage.type !== "PROPOSAL") {
      return null;
    }

    // PROPOSAL 이후 ACCEPTED / REJECTED가 있는지 확인
    const hasReplied = allMessages.some(
      (m) =>
        (m.type === "ACCEPTED" || m.type === "REJECTED") &&
        new Date(m.createdAt) > new Date(lastSystemMessage.createdAt),
    );

    return hasReplied ? null : lastSystemMessage.chatMessageId;
  }, [lastSystemMessage, allMessages]);

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
    });
  }, [allMessages, isConnected, userId, chatRoomId, sendMessage, isRoomActive]);

  // 6. 채팅방 수임 상태 실시간 반영
  // initialRoomStatus가 있으면 그것을 초기값으로 사용
  // 시스템 메시지가 있으면 그것으로 업데이트
  const chatRoomStatus: ChatRoomStatus = useMemo(() => {
    if (lastSystemMessage) {
      switch (lastSystemMessage.type) {
        case "PROPOSAL":
          return "PROPOSED";
        case "ACCEPTED":
          return "MATCHED";
        case "CHATROOM_BLOCKED":
          return "CHATROOM_BLOCKED";
        default:
          return "DEFAULT";
      }
    }

    // 시스템 메시지가 없으면 초기 상태 사용
    if (initialRoomStatus) {
      const statusMap: Record<string, ChatRoomStatus> = {
        DEFAULT: "DEFAULT",
        PROPOSED: "PROPOSED",
        MATCHED: "MATCHED",
        REJECTED: "REJECTED",
        CANCELED: "CANCELED",
        CHATROOM_BLOCKED: "CHATROOM_BLOCKED",
      };
      return statusMap[initialRoomStatus] || "DEFAULT";
    }

    return "DEFAULT";
  }, [lastSystemMessage, initialRoomStatus]);

  const proposedByMe = useMemo(() => {
    if (!lastSystemMessage) return false;
    if (lastSystemMessage.type === "PROPOSAL" && lastSystemMessage.isSentByMe)
      return true;
    return false;
  }, [lastSystemMessage]);

  const documentId = useMemo(() => {
    if (!lastSystemMessage) return null;
    if (lastSystemMessage.type === "ACCEPTED") return lastSystemMessage.content;
    return null;
  }, [lastSystemMessage]);

  // 7. 자동 스크롤
  useEffect(() => {
    if (!scrollRef.current) return;

    if (isAtBottom) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
    }
  }, [allMessages, isAtBottom]);

  return {
    groupedChats,
    chatRoomStatus,
    isLoading,
    isError,
    scrollRef,
    handleScroll,
    isFetchingNextPage,
    proposedByMe,
    pendingProposalId, // UI에서 어떤 메시지에 버튼을 띄울지 결정하는 ID
    documentId, // ACCEPTED 메시지의 content를 문서 ID로 활용
  };
};

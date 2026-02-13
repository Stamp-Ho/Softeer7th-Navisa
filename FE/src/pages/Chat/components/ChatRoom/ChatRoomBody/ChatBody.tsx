import { useEffect, useMemo, useRef } from "react";
import Tag from "../../../../../components/common/Tag";
import CalcChattedTime from "../../../../../utils/CalcChattedTime";
import ChatSystemMessage from "./ChatSystemMessage";
import { useAuth } from "../../../../../contexts/AuthContextProvider";
import { useChatHistoryQuery } from "../../../../../api/hooks/useChatHistoryQuery";
import type { ChatHistoryResponse } from "../../../../../api/types/chat";
import { useWebSocket } from "../../../../../contexts/WebSocketContext";
import type { Message } from "../../../../../api/websocket/types";
import CalcDateSystemMessage from "../../../../../utils/CalcDateSystemMessage";
import groupChatLogs from "../../../../../utils/GroupChatLogs";
import { v4 as uuidv4 } from "uuid";

type ChatBodyParams = {
  chatRoomId: number;
  onModalAction: (num: number) => void;
  opponentName: string;
  myName: string;
  profileImg: string | null;
};

const ChatBody = ({
  chatRoomId,
  onModalAction,
  opponentName,
  myName,
  profileImg,
}: ChatBodyParams) => {
  const scrollRef = useRef<HTMLDivElement>(null);
  const { userId, userType } = useAuth();
  const isAgent = userType === "VALID_AGENT";
  const { sendMessage } = useWebSocket();

  // ===== 기존 채팅 내역 불러오기 =====
  const { data, isLoading, isError } = useChatHistoryQuery(chatRoomId);

  // ===== 웹소켓 메시지 =====
  const { messages } = useWebSocket();
  const roomMessages = useMemo(
    () => messages.filter((m: Message) => Number(m.roomId) === chatRoomId),
    [messages, chatRoomId],
  );
  //======================================================
  const lastReadSentRef = useRef<number | null>(null);
  useEffect(() => {
    if (!data || !userId || data.length === 0) return;

    const lastMessage = data[0];
    if (!lastMessage?.chatMessageId) return;

    // 이미 보낸 적 있으면 중복 방지
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
  }, [data, chatRoomId]);
  useEffect(() => {
    if (!userId || roomMessages.length === 0) return;

    const lastMessage = roomMessages[roomMessages.length - 1];
    console.log(lastMessage);
    if (!lastMessage?.messageId) return;

    const byMe = lastMessage.senderId === userId;
    console.log(lastMessage.senderId);
    console.log(userId);
    console.log(byMe);
    if (byMe) return;

    // 중복 방지
    if (lastReadSentRef.current === lastMessage.messageId) return;
    lastReadSentRef.current = lastMessage.messageId;

    sendMessage({
      roomId: chatRoomId,
      clientMessageId: uuidv4(),
      senderId: userId,
      content: String(lastMessage.messageId),
      type: "READ",
      sentAt: new Date().toISOString(),
    });
  }, [roomMessages]);
  //======================================================

  // ===== 자동 스크롤 =====
  useEffect(() => {
    if (!scrollRef.current || !data) return;
    scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
  }, [data, roomMessages]);

  if (isLoading) return <div className="p-6">채팅 불러오는 중...</div>;
  if (isError || !data)
    return <div className="p-6 text-red-500">채팅을 불러오지 못했습니다.</div>;

  // WebSocket Message -> 기존 ChatHistoryResponse 형태로 변환
  const realtimeAsHistory: ChatHistoryResponse[] = roomMessages.map((m) => ({
    chatMessageId: m.messageId, // 서버 기준 ID 사용
    isSentByMe: m.senderId === userId ? true : false, // 수신자가 내가 아니면 = 내가 보낸 메시지
    type: m.type,
    content: m.content,
    sentAt: m.sentAt,
    createdAt: m.createdAt,
    isRead: m.isRead,
  }));

  // ===== 기존 히스토리 + 웹소켓 병합 =====
  const mergedMessages: ChatHistoryResponse[] = [...data, ...realtimeAsHistory]
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

  // ===== 그룹화 =====
  const groupedChats = groupChatLogs(mergedMessages);

  // ===== 마지막 수임 제안 메시지에만 답변 버튼 표시 =====
  // 1) 내가 받은 최신 PROPOSAL 찾기
  const latestProposal = [...mergedMessages]
    .reverse()
    .find((m) => m.type === "PROPOSAL" /*&& m.isSentByMe === false*/);

  // 2) 그 이후에 내가 답변(ACCEPTED/REJECTED) 했는지 확인
  const hasRepliedAfterProposal =
    !!latestProposal &&
    mergedMessages.some(
      (m) =>
        (m.type === "ACCEPTED" || m.type === "REJECTED") &&
        // m.isSentByMe === true &&
        new Date(m.createdAt) > new Date(latestProposal.createdAt),
    );

  // 3) 최종: 지금 버튼이 있어야 할 메시지 ID
  const pendingProposalId =
    latestProposal && !hasRepliedAfterProposal
      ? latestProposal.chatMessageId
      : null;

  return (
    <div ref={scrollRef} className="overflow-auto scrollbar-hide">
      {groupedChats.map((group, index) =>
        group[0].type === "SYSTEM" ? (
          <div
            key={group[0].chatMessageId}
            className="flex flex-row justify-center w-full my-10"
          >
            <Tag type="small_fill_gray">
              {CalcDateSystemMessage(group[0].createdAt)}
            </Tag>
          </div>
        ) : (
          <div
            key={index}
            className={`flex flex-col px-6 ${
              !group[0].isSentByMe ? "items-start" : "items-end"
            }`}
          >
            <div className="flex flex-row gap-3">
              {!group[0].isSentByMe &&
                (isAgent ? (
                  <div className="flex flex-row justify-center items-center w-[56px] h-[56px] rounded-full bg-violet-25 title-l-bold text-violet-500">
                    {opponentName[0]}
                  </div>
                ) : (
                  <img
                    src={profileImg ?? "https://placehold.co/56x56"}
                    alt="행정사 프로필 사진"
                    className="w-[56px] h-[56px] mr-2 object-cover rounded-full"
                  />
                ))}

              <div>
                {group.map((chatLog, idx) => (
                  <div
                    key={chatLog.chatMessageId}
                    className={`flex flex-row gap-3 mb-2 ${
                      group[0].isSentByMe ? "justify-end" : "justify-start"
                    }`}
                  >
                    {idx === group.length - 1 && group[0].isSentByMe && (
                      <div className="flex flex-col gap-[2px] justify-end items-end caption-l-regular text-text-sub">
                        <div>
                          {!group[group.length - 1].isRead && "안 읽음"}
                        </div>
                        <div>{CalcChattedTime(group[0].createdAt)}</div>
                      </div>
                    )}

                    {chatLog.type === "TEXT" && (
                      <div
                        className={`max-w-[500px] px-6 py-5 bg-background-sub rounded-b-[10px] whitespace-pre-wrap ${
                          chatLog.isSentByMe
                            ? "rounded-tl-[10px] rounded-tr-[2px]"
                            : "rounded-tl-[2px] rounded-tr-[10px]"
                        }`}
                      >
                        {chatLog.content}
                      </div>
                    )}

                    {chatLog.type === "PROPOSAL" && (
                      <ChatSystemMessage
                        type="PROPOSAL"
                        onModalAction={onModalAction}
                        senderName={chatLog.isSentByMe ? myName : opponentName}
                        receiverName={
                          chatLog.isSentByMe ? opponentName : myName
                        }
                        isSentByMe={chatLog.isSentByMe}
                        showReplyButton={
                          pendingProposalId === chatLog.chatMessageId
                        }
                      />
                    )}

                    {chatLog.type === "ACCEPTED" && (
                      <ChatSystemMessage
                        type="ACCEPTED"
                        onModalAction={onModalAction}
                        senderName={chatLog.isSentByMe ? myName : opponentName}
                        receiverName={
                          chatLog.isSentByMe ? opponentName : myName
                        }
                      />
                    )}

                    {chatLog.type === "REJECTED" && (
                      <ChatSystemMessage
                        type="REJECTED"
                        onModalAction={onModalAction}
                      />
                    )}

                    {chatLog.type === "CANCELED" && (
                      <ChatSystemMessage
                        type="CANCELED"
                        senderName={chatLog.isSentByMe ? myName : opponentName}
                        receiverName={isAgent ? myName : opponentName}
                        onModalAction={onModalAction}
                      />
                    )}

                    {idx === group.length - 1 && !group[0].isSentByMe && (
                      <div className="flex items-end caption-l-regular text-text-sub">
                        {CalcChattedTime(group[0].createdAt)}
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </div>
            <div className="mb-7"></div>
          </div>
        ),
      )}
    </div>
  );
};

export default ChatBody;

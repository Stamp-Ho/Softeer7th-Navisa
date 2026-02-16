import ChatSystemMessage from "./ChatSystemMessage"; // 기존 컴포넌트 재사용
import type { ChatHistoryResponse } from "../../../../../api/types/chat";
import CalcChattedTime from "../../../../../utils/CalcChattedTime";

type ChatBubbleProps = {
  message: ChatHistoryResponse;
  opponentName: string;
  myName: string;
  isAgent: boolean;
  onModalAction: (num: number) => void;
  showReplyButton: boolean;
  isRead: boolean;
  isLast: boolean;
};

const ChatBubble = ({
  message,
  opponentName,
  myName,
  onModalAction,
  showReplyButton,
  isRead,
  isLast,
}: ChatBubbleProps) => {
  const { type, content, isSentByMe } = message;

  if (type === "TEXT") {
    return (
      <div className="flex flex-row gap-3 items-end">
        {/* 내 메시지일 때 시간/읽음 표시 (좌측) */}
        <div className="flex flex-col gap-[2px] justify-end items-end caption-l-regular text-text-sub">
          {isSentByMe && !isRead && <div>안 읽음</div>}
          {isSentByMe && isLast && (
            <div>{CalcChattedTime(message.createdAt)}</div>
          )}
        </div>

        <div
          className={`max-w-[500px] px-6 py-5 bg-background-sub whitespace-pre-wrap ${
            isSentByMe
              ? "rounded-tl-[10px] rounded-tr-[2px] rounded-b-[10px]"
              : "rounded-tl-[2px] rounded-tr-[10px] rounded-b-[10px]"
          }`}
        >
          {content}
        </div>

        {/* 상대 메시지일 때 시간 표시 (우측) */}
        <div className="flex flex-col gap-[2px] caption-l-regular text-text-sub">
          {!isSentByMe && isLast && (
            <div>{CalcChattedTime(message.createdAt)}</div>
          )}
        </div>
      </div>
    );
  }

  // 시스템 메시지류 (PROPOSAL, ACCEPTED, REJECTED, CANCELED)
  if (["PROPOSAL", "ACCEPTED", "REJECTED", "CANCELED"].includes(type)) {
    return (
      <ChatSystemMessage
        type={type as any}
        onModalAction={onModalAction}
        senderName={isSentByMe ? myName : opponentName}
        receiverName={isSentByMe ? opponentName : myName}
        isSentByMe={isSentByMe}
        showReplyButton={showReplyButton}
      />
    );
  }

  return null;
};

export default ChatBubble;

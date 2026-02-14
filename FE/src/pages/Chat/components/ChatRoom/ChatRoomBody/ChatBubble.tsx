// components/ChatBubble.tsx
import ChatSystemMessage from "./ChatSystemMessage"; // 기존 컴포넌트 재사용
import type { ChatHistoryResponse } from "../../../../../api/types/chat";

type ChatBubbleProps = {
  message: ChatHistoryResponse;
  opponentName: string;
  myName: string;
  isAgent: boolean;
  onModalAction: (num: number) => void;
  showReplyButton: boolean;
};

const ChatBubble = ({
  message,
  opponentName,
  myName,
  onModalAction,
  showReplyButton,
}: ChatBubbleProps) => {
  const { type, content, isSentByMe } = message;

  if (type === "TEXT") {
    return (
      <div
        className={`max-w-[500px] px-6 py-5 bg-background-sub whitespace-pre-wrap ${
          isSentByMe
            ? "rounded-tl-[10px] rounded-tr-[2px] rounded-b-[10px]"
            : "rounded-tl-[2px] rounded-tr-[10px] rounded-b-[10px]"
        }`}
      >
        {content}
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

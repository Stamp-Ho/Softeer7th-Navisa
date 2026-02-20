import { useTranslation } from "react-i18next";
import ChatSystemMessage from "./ChatSystemMessage"; // 기존 컴포넌트 재사용
import type { ChatHistoryResponse } from "../../../../../api/types/chat";
import CalcChattedTime from "../../../../../utils/CalcChattedTime";

type ChatBubbleProps = {
  pageType: "CHAT" | "DOCUMENT";
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
  pageType,
  message,
  opponentName,
  myName,
  isAgent,
  onModalAction,
  showReplyButton,
  isRead,
  isLast,
}: ChatBubbleProps) => {
  const { t } = useTranslation(["components"]);
  const { type, content, isSentByMe } = message;
  const size = pageType === "DOCUMENT" ? "max-w-[300px]" : "max-w-[500px]";

  return (
    <div className="flex flex-row gap-3 items-end">
      {/* 내 메시지일 때 시간/읽음 표시 (좌측) */}
      {isSentByMe && (
        <div className="flex flex-col gap-[2px] justify-end items-end caption-l-regular text-text-sub">
          {!isRead && <div>{t("chatRoom.unread")}</div>}
          {isLast && <div>{CalcChattedTime(message.sentAt)}</div>}
        </div>
      )}

      {type === "TEXT" && (
        <div
          className={`${size} px-6 ${pageType === "DOCUMENT" ? "py-3" : "py-5"} bg-background-sub whitespace-pre-wrap break-words ${
            isSentByMe
              ? "rounded-tl-[10px] rounded-tr-[2px] rounded-b-[10px]"
              : "rounded-tl-[2px] rounded-tr-[10px] rounded-b-[10px]"
          }`}
        >
          {content}
        </div>
      )}

      {["PROPOSAL", "ACCEPTED", "REJECTED", "CANCELED"].includes(type) && (
        <ChatSystemMessage
          type={type as any}
          onModalAction={onModalAction}
          senderName={isSentByMe ? myName : opponentName}
          agentName={isAgent ? myName : opponentName}
          isSentByMe={isSentByMe}
          showReplyButton={showReplyButton}
          pageType={pageType}
        />
      )}

      {/* 상대 메시지일 때 시간 표시 (우측) */}
      {!isSentByMe && isLast && (
        <div className="flex flex-col gap-[2px] caption-l-regular text-text-sub">
          {CalcChattedTime(message.sentAt)}
        </div>
      )}
    </div>
  );
};

export default ChatBubble;

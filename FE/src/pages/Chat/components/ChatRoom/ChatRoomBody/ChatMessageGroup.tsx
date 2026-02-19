import { useTranslation } from "react-i18next";
import ChatBubble from "./ChatBubble";
import type { ChatHistoryResponse } from "../../../../../api/types/chat";

type Props = {
  group: ChatHistoryResponse[];
  opponentName: string;
  myName: string;
  profileImg: string | null;
  isAgent: boolean;
  onModalAction: (num: number) => void;
  pendingProposalId: number | null;
};

const ChatMessageGroup = ({
  group,
  opponentName,
  myName,
  profileImg,
  isAgent,
  onModalAction,
  pendingProposalId,
}: Props) => {
  const { t } = useTranslation(["components"]);
  const firstMsg = group[0];
  const isMe = firstMsg.isSentByMe;

  // 상대방 프로필 렌더링 헬퍼
  const renderProfile = () => {
    if (isMe) return null;
    if (isAgent) {
      return (
        <div className="flex flex-row justify-center items-center w-[56px] h-[56px] rounded-full bg-violet-25 title-l-bold text-violet-500 shrink-0">
          {opponentName[0] ?? "?"}
        </div>
      );
    }
    return (
      <img
        src={profileImg ?? "https://placehold.co/56x56"}
        alt={t("chatRoom.profileImageAlt")}
        className="w-[56px] h-[56px] mr-2 object-cover rounded-full shrink-0"
      />
    );
  };

  return (
    <div
      className={`flex flex-col px-6 mb-7 ${!isMe ? "items-start" : "items-end"}`}
    >
      <div className="flex flex-row gap-3">
        {renderProfile()}

        <div className="flex flex-col">
          {group.map((msg, idx) => {
            const isLast = idx === group.length - 1;

            return (
              <div
                key={msg.chatMessageId}
                className={`flex flex-row gap-3 mb-2 ${isMe ? "justify-end" : "justify-start"}`}
              >
                {/* 말풍선 내용 */}
                <ChatBubble
                  message={msg}
                  opponentName={opponentName}
                  myName={myName}
                  isAgent={isAgent}
                  onModalAction={onModalAction}
                  showReplyButton={pendingProposalId === msg.chatMessageId}
                  isRead={msg.isRead}
                  isLast={isLast}
                />
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};

export default ChatMessageGroup;

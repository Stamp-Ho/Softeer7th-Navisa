import { useTranslation } from "react-i18next";
import ChatBubble from "./ChatBubble";
import type { ChatHistoryResponse } from "../../../../../api/types/chat";

type Props = {
  pageType: "CHAT" | "DOCUMENT";
  group: ChatHistoryResponse[];
  opponentName: string;
  myName: string;
  profileImg: string | null;
  isAgent: boolean;
  reviewHandler: (num: number) => void;
  onModalAction: (num: number) => void;
  showReviewModal?: (show: boolean, isFeedback?: boolean) => void;
  pendingProposalId: number | null;
  matchingEndRequired?: boolean;
};

const ChatMessageGroup = ({
  pageType,
  group,
  opponentName,
  myName,
  profileImg,
  isAgent,
  reviewHandler,
  onModalAction,
  showReviewModal,
  pendingProposalId,
  matchingEndRequired,
}: Props) => {
  const { t } = useTranslation(["components"]);
  const firstMsg = group[0];
  const isMe = firstMsg.isSentByMe;

  // 상대방 프로필 렌더링 헬퍼
  const renderProfile = () => {
    if (isMe) return null;
    const size =
      pageType === "CHAT" ? "w-[56px] h-[56px]" : "w-[40px] h-[40px]";
    if (isAgent) {
      return (
        <div
          className={`flex flex-row justify-center items-center rounded-full bg-violet-25 title-l-bold text-violet-500 shrink-0 ${size}`}
        >
          {opponentName[0] ?? "?"}
        </div>
      );
    }
    return (
      <img
        src={
          profileImg ??
          (pageType === "CHAT"
            ? "https://placehold.co/56x56"
            : "https://placehold.co/40x40")
        }
        alt={t("chatRoom.profileImageAlt")}
        className={`mr-2 object-cover rounded-full flex-shrink-0 ${size}`}
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
                  reviewHandler={reviewHandler}
                  onModalAction={onModalAction}
                  showReviewModal={showReviewModal}
                  showReplyButton={pendingProposalId === msg.chatMessageId}
                  isRead={msg.isRead}
                  isLast={isLast}
                  pageType={pageType}
                  matchingEndRequired={matchingEndRequired}
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

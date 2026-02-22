import { useTranslation } from "react-i18next";
import Tag from "../../../../../components/common/Tag";
import CalcDateSystemMessage from "../../../../../utils/CalcDateSystemMessage";
import { useAuth } from "../../../../../contexts/AuthContextProvider";
import ChatMessageGroup from "./ChatMessageGroup"; // 분리된 컴포넌트
import { useChatRoomContext } from "../../context/ChatRoomContext";

type ChatBodyParams = {
  pageType: "CHAT" | "DOCUMENT";
  reviewHandler: (num: number) => void;
  onModalAction: (num: number) => void;
  showReviewModal?: (show: boolean, isFeedback?: boolean) => void;
  opponentName?: string;
  myName?: string;
  profileImg: string | null;
  matchingEndRequired?: boolean;
};

const ChatBody = ({
  pageType,
  reviewHandler,
  onModalAction,
  showReviewModal,
  opponentName = "loading",
  myName = "loading",
  profileImg,
  matchingEndRequired,
}: ChatBodyParams) => {
  const { t } = useTranslation(["components"]);
  const { userType } = useAuth();
  const isAgent = userType === "VALID_AGENT";
  const {
    groupedChats,
    isLoading,
    isError,
    scrollRef,
    pendingProposalId,
    handleScroll,
    isFetchingNextPage,
  } = useChatRoomContext();

  if (isLoading) return <div className="p-6">{t("chatRoom.loadingChat")}</div>;
  if (isError)
    return (
      <div className="p-6 text-red-500">{t("chatRoom.loadChatFailed")}</div>
    );

  return (
    <>
      <div
        ref={scrollRef}
        onScroll={handleScroll}
        style={{
          overflowY: "auto",
          overflowAnchor: "none",
        }}
        className="scrollbar-hide h-full"
      >
        <div style={{ height: pageType === "DOCUMENT" ? 200 : 600 }}>
          {isFetchingNextPage && t("chatRoom.previousMessagesLoading")}
        </div>
        {groupedChats.map((group) => {
          const firstMsg = group[0];

          // 날짜 구분선 (SYSTEM 메시지)
          if (firstMsg.type === "SYSTEM") {
            return (
              <div
                key={firstMsg.chatMessageId}
                className="flex flex-row justify-center items-center my-10 gap-3 mx-3"
              >
                <div className="py-[1px] bg-gray-100 flex-1" />
                <Tag variant="small_fill_gray">
                  {CalcDateSystemMessage(firstMsg.createdAt)}
                </Tag>
                <div className="py-[1px] bg-gray-100 flex-1" />
              </div>
            );
          }

          // 일반 메시지 그룹
          return (
            <ChatMessageGroup
              pageType={pageType}
              key={`group-${firstMsg.chatMessageId}`}
              group={group}
              opponentName={opponentName}
              myName={myName}
              profileImg={profileImg}
              isAgent={isAgent}
              reviewHandler={reviewHandler}
              onModalAction={onModalAction}
              pendingProposalId={pendingProposalId}
              matchingEndRequired={matchingEndRequired}
              showReviewModal={showReviewModal}
            />
          );
        })}
      </div>
    </>
  );
};

export default ChatBody;

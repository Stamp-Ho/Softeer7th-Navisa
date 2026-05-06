import { useTranslation } from "react-i18next";
import Tag from "../../../../../components/common/Tag";
import CalcDateSystemMessage from "../../../../../utils/CalcDateSystemMessage";
import { useAuth } from "../../../../../contexts/AuthContextProvider";
import ChatMessageGroup from "./ChatMessageGroup"; // 분리된 컴포넌트
import { useChatRoomContext } from "../../context/ChatRoomContext";
import { useEffect, useRef, useState } from "react";
import Envelope from "../../../../../assets/Envelope";

type ChatBodyParams = {
  pageType: "CHAT" | "DOCUMENT";
  reviewHandler: (num: number) => void;
  onModalAction: (num: number) => void;
  showReviewModal?: (show: boolean, isFeedback?: boolean) => void;
  opponentName?: string;
  myName?: string;
  profileImg: string | null;
  proposalEndRequired?: boolean;
  feedbackSubmitted: boolean;
};

const ChatBody = ({
  pageType,
  reviewHandler,
  onModalAction,
  showReviewModal,
  opponentName = "loading",
  myName = "loading",
  profileImg,
  proposalEndRequired,
  feedbackSubmitted,
}: ChatBodyParams) => {
  const { t } = useTranslation(["components"]);
  const { userType } = useAuth();
  const isAgent = userType === "VALID_AGENT";
  const { groupedChats, isLoading, isError, scrollRef, pendingProposalId, handleScroll, isFetchingNextPage } =
    useChatRoomContext();

  const innerContainer = useRef<HTMLDivElement>(null);

  const [topPadding, setTopPadding] = useState(200); // 초기 패딩값
  useEffect(() => {
    const innerTarget = innerContainer.current;
    if (!innerTarget) return;

    const handleResize = () => {
      const { clientHeight } = innerTarget;
      if (clientHeight < 600) setTopPadding(600 - clientHeight);
      else setTopPadding(60);
    };

    const resizeObserver = new ResizeObserver(handleResize);
    resizeObserver.observe(innerTarget);

    return () => {
      resizeObserver.disconnect();
    };
  }, [groupedChats]);

  const [showLoading, setShowLoading] = useState(false);

  useEffect(() => {
    if (isLoading) {
      // 500ms 이상 로딩이 지속되면 로딩 표시
      const timeoutId = setTimeout(() => {
        setShowLoading(true);
      }, 200);

      return () => {
        clearTimeout(timeoutId);
        setShowLoading(false);
      };
    } else {
      setShowLoading(false);
    }
  }, [isLoading]);
  if (showLoading)
    return (
      <div className="p-6 body-l-bold text-green-500 flex flex-col items-center justify-center w-full animate-pulse">
        <Envelope />
        {t("chatRoom.loadingChat")}
      </div>
    );
  if (isError)
    return (
      <div className="p-6 body-l-bold text-red-500 flex flex-col items-center justify-center w-full">
        <Envelope />
        {t("chatRoom.loadChatFailed")}
      </div>
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
        <div style={{ height: pageType === "DOCUMENT" ? 200 : topPadding }}>
          {isFetchingNextPage && t("chatRoom.previousMessagesLoading")}
        </div>
        <div ref={innerContainer}>
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
                  <Tag variant="small_fill_gray">{CalcDateSystemMessage(firstMsg.createdAt)}</Tag>
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
                proposalEndRequired={proposalEndRequired}
                showReviewModal={showReviewModal}
                feedbackSubmitted={feedbackSubmitted}
              />
            );
          })}
        </div>
      </div>
    </>
  );
};

export default ChatBody;

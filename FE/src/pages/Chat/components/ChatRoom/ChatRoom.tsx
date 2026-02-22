import type { ChatRoomHeaderData } from "../../../../types/chatRoomTypes";
import ChatBody from "./ChatRoomBody/ChatBody";
import ChatRoomFooter from "./ChatRoomFooter/ChatRoomFooter";
import ChatRoomHeader from "./ChatRoomHeader/ChatRoomHeader";
import { useAuth } from "../../../../contexts/AuthContextProvider";
import { useChatParticipantsInfoQuery } from "../../../../api/queries/useChatParticipantsInfoQuery";
import { ChatRoomProvider } from "../context/ChatRoomContext";
import ReviewModal from "../Review/ReviewModal";
import { useState, useEffect } from "react";
import { useSearchParams } from "react-router-dom";

type ChatRoomParams = {
  pageType?: "CHAT" | "DOCUMENT";
  chatRoomId: number;
  onClose: () => void;
  onModalAction: (num: number) => void;
  profileImg: string | null;
  onGoToChat?: () => void;
  initialRoomStatus?: string;
};

const ChatRoom = ({
  pageType = "CHAT",
  chatRoomId,
  onClose,
  onModalAction,
  profileImg,
  onGoToChat,
  initialRoomStatus,
}: ChatRoomParams) => {
  const { userType } = useAuth();
  const isAgent = userType === "VALID_AGENT";
  const { data: participants } = useChatParticipantsInfoQuery(chatRoomId); // 추가로 행정사가 수임종료 응답을 해야하는지 여부 받음 > 수임종료 모달을 띄울 예정
  const [reviewModal, setReviewModal] = useState<number>(0);

  const [isReviewRequired, setIsReviewRequired] = useState<boolean>(false);
  const [isVisaModalShown, setIsVisaModalShown] = useState(false);
  const [isFeedbackRequired, setIsFeedbackRequired] = useState(false); // FEEDBACK_REQUIRED 메시지에서 호출되었는지 여부
  const [searchParams] = useSearchParams();
  const chatRoomNumber = Number(searchParams.get("chatroom"));
  const reviewHandler = (num: number) => {
    setReviewModal(num);
    setIsVisaModalShown(num > 0);
  };

  useEffect(() => {
    if (participants?.foreignerInfo?.isReviewRequired !== undefined) {
      setIsReviewRequired(participants.foreignerInfo.isReviewRequired);
    }
  }, [participants]);

  const matchingEndRequired =
    participants?.agentInfo?.matchingEndRequired || false; // 수임종료 모달 띄울지 여부 (행정사 응답 필요 여부)
  // matchingEndRequired가 true이고 행정사인 경우 자동으로 VisaResponseModal 표시
  useEffect(() => {
    if (
      participants &&
      isAgent &&
      !isVisaModalShown &&
      matchingEndRequired &&
      chatRoomNumber === chatRoomId
    ) {
      setReviewModal(3);
      setIsVisaModalShown(true);
    }
  }, [participants, isAgent, isVisaModalShown]);

  const headerData: ChatRoomHeaderData = isAgent
    ? { type: "FOREIGNER", data: participants?.foreignerInfo }
    : { type: "AGENT", data: participants?.agentInfo };

  return (
    <ChatRoomProvider
      chatRoomId={chatRoomId}
      isActive={true}
      initialRoomStatus={initialRoomStatus}
    >
      <ChatRoomHeader
        reviewHandler={reviewHandler}
        pageType={pageType}
        headerData={headerData}
        onClose={onClose}
        onGoToChat={onGoToChat}
        onModalAction={onModalAction}
        isReviewRequired={isReviewRequired}
      />
      <div className="w-full pt-10" />
      <ChatBody
        pageType={pageType}
        reviewHandler={reviewHandler}
        onModalAction={onModalAction}
        profileImg={profileImg}
        opponentName={
          isAgent
            ? participants?.foreignerInfo?.nickname
            : participants?.agentInfo?.name
        }
        myName={
          isAgent
            ? participants?.agentInfo?.name
            : participants?.foreignerInfo?.nickname
        }
        matchingEndRequired={matchingEndRequired}
        showReviewModal={(show: boolean, isFeedback: boolean = false) => {
          setIsVisaModalShown(show);
          setIsFeedbackRequired(isFeedback);
          // reviewModal 상태가 0이 아닌 경우에만 유지, 0인 경우 BadgeReviewModal으로 설정
          if (show && reviewModal === 0) {
            reviewHandler(1);
          }
        }}
      />
      <div className="w-full pt-28" />
      <ChatRoomFooter
        chatRoomId={chatRoomId}
        pageType={pageType}
        applicationFormId={participants?.agentInfo?.applicationFormId}
      />

      {isVisaModalShown && participants && (
        <div className="absolute">
          <ReviewModal
            reviewHandler={reviewHandler}
            modalView={reviewModal}
            agentId={participants?.agentInfo?.agentId || ""}
            setIsReviewRequired={setIsReviewRequired}
            formId={participants?.agentInfo?.applicationFormId || ""}
            isAgent={isAgent}
            matchingEndRequired={matchingEndRequired}
            isFeedbackRequired={isFeedbackRequired}
          />
        </div>
      )}
    </ChatRoomProvider>
  );
};

export default ChatRoom;

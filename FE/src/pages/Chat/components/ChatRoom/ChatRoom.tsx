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
import { useWebSocket } from "../../../../contexts/WebSocketContext";

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
  const { data: participants, refetch: refetchParticipants } = useChatParticipantsInfoQuery(chatRoomId);
  const { registerParticipantsInfoCallback, unregisterParticipantsInfoCallback } = useWebSocket();
  const [reviewModal, setReviewModal] = useState<number>(0); // 0: none, 1: badge review 작성, 2: service review 작성, 3: 수임 종료 모달
  const [isReviewRequired, setIsReviewRequired] = useState<boolean>(false);
  const [isVisaModalShown, setIsVisaModalShown] = useState(false);
  const [isFeedbackRequired, setIsFeedbackRequired] = useState(false); // FEEDBACK_REQUIRED 메시지에서 호출되었는지 여부
  const [feedbackSubmitted, setFeedbackSubmitted] = useState(false); // 피드백 제출 완료 여부
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

  // ParticipantsInfo 동기화: REVIEW_REQUIRED 또는 FEEDBACK_REQUIRED 메시지 수신 시 refetch
  // FEEDBACK_REQUIRED 메시지 도착 시 feedbackSubmitted 상태 리셋
  useEffect(() => {
    registerParticipantsInfoCallback(chatRoomId, () => {
      refetchParticipants();
      setFeedbackSubmitted(false); // FEEDBACK_REQUIRED 메시지 도착 시 피드백 버튼 다시 표시
    });

    return () => {
      unregisterParticipantsInfoCallback(chatRoomId);
    };
  }, [chatRoomId, registerParticipantsInfoCallback, unregisterParticipantsInfoCallback, refetchParticipants]);

  const proposalEndRequired = participants?.proposalEndRequired || false; // 수임종료 모달 띄울지 여부 (행정사 응답 필요 여부)
  // proposalEndRequired가 true이고 행정사인 경우 자동으로 VisaResponseModal 표시
  useEffect(() => {
    if (participants && isAgent && !isVisaModalShown && proposalEndRequired && chatRoomNumber === chatRoomId) {
      setReviewModal(3);
      setIsVisaModalShown(true);
    }
  }, [participants, isAgent, isVisaModalShown]);

  const headerData: ChatRoomHeaderData = isAgent
    ? { type: "FOREIGNER", data: participants?.foreignerInfo }
    : { type: "AGENT", data: participants?.agentInfo };

  return (
    <ChatRoomProvider chatRoomId={chatRoomId} isActive={true} initialRoomStatus={initialRoomStatus}>
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
        opponentName={isAgent ? participants?.foreignerInfo?.nickname : participants?.agentInfo?.name}
        myName={isAgent ? participants?.agentInfo?.name : participants?.foreignerInfo?.nickname}
        proposalEndRequired={proposalEndRequired}
        feedbackSubmitted={feedbackSubmitted}
        showReviewModal={(show: boolean, isFeedback: boolean = false) => {
          setIsVisaModalShown(show);
          setIsFeedbackRequired(isFeedback);
          // FEEDBACK_REQUIRED에서 호출되었거나 이미 모달이 열려있으면 무시
          // (reviewHandler에서 이미 모달을 관리함)
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
            proposalEndRequired={proposalEndRequired}
            isFeedbackRequired={isFeedbackRequired}
            setFeedbackSubmitted={setFeedbackSubmitted}
          />
        </div>
      )}
    </ChatRoomProvider>
  );
};

export default ChatRoom;

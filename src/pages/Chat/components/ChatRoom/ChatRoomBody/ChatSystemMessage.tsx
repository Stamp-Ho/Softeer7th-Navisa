import { useTranslation } from "react-i18next";
import { IcMessageBox, IcMessageBoxCross } from "../../../../../assets/icon/StratisUi";
import ChatSystemMessageBackground from "../../../../../assets/ChatSystemMessageBackground";
import { useChatRoomContext } from "../../context/ChatRoomContext";
import { Link } from "react-router-dom";
import { useForeignerProgressQuery } from "../../../../../api/queries/useForeignerProgressQuery";
import { useAuth } from "../../../../../contexts/AuthContextProvider";

type ChatSystemMessageParams = {
  pageType: "CHAT" | "DOCUMENT";
  type: "PROPOSAL" | "ACCEPTED" | "REJECTED" | "CANCELED" | "FEEDBACK_REQUIRED";
  senderName?: string;
  date?: string;
  isSentByMe?: boolean;
  reviewHandler: (num: number) => void;
  onModalAction: (num: number) => void;
  showReviewModal?: (show: boolean, isFeedback?: boolean) => void;
  showReplyButton?: boolean;
  agentName: string;
  proposalEndRequired?: boolean;
  feedbackSubmitted: boolean;
};

const ChatSystemMessage = ({
  pageType,
  type,
  senderName,
  isSentByMe,
  reviewHandler,
  onModalAction,
  showReviewModal,
  showReplyButton,
  agentName,
  proposalEndRequired,
  feedbackSubmitted,
}: ChatSystemMessageParams) => {
  const size = pageType === "DOCUMENT" ? "w-[300px]" : "w-[368px]";
  const { t } = useTranslation(["components"]);
  const { userType } = useAuth();
  const { chatRoomStatus, documentId } = useChatRoomContext(); // 채팅방 상태 가져오기
  const { data: reviewProgress } = useForeignerProgressQuery({
    enabled: userType === "FILLED_FOREIGNER",
    type,
  });

  switch (type) {
    case "PROPOSAL":
      return (
        <div className={`flex flex-col rounded-[12px] border-[1.5px] border-violet-50 overflow-hidden ${size}`}>
          <div className="object-fit">
            <ChatSystemMessageBackground pageType={pageType} />
          </div>

          <div className="flex flex-col gap-5 p-6 bg-gray-0 text-text-base">
            <div className="flex flex-col gap-2">
              <div className="body-l-bold">{t("chatRoom.proposalSent", { name: senderName ?? "" })}</div>
              <div className="body-s-medium">{t("chatRoom.replyViaButton")}</div>
            </div>
            {!isSentByMe && showReplyButton && chatRoomStatus !== "CHATROOM_BLOCKED" && (
              <button
                className="body-s-semibold rounded-[6px] h-[48px] bg-gray-50 text-text-base cursor-pointer"
                onClick={() => onModalAction(3)}
              >
                {t("chatRoom.sendReply")}
              </button>
            )}
          </div>
        </div>
      );

    case "ACCEPTED":
      return (
        <div
          className={`flex flex-col gap-5 p-6 border-[1.5px] border-violet-50 text-violet-500 rounded-[12px] bg-gray-0 ${size}`}
        >
          <div className="flex flex-row items-center gap-4">
            <IcMessageBox color="var(--violet-500)" />
            <div className="flex flex-col gap-[1px]">
              <div className="body-l-bold">{t("chatRoom.retainerAccepted")}</div>
              <div className="body-s-medium whitespace-pre-wrap">{t("chatRoom.canWriteVisaTogether")}</div>
            </div>
          </div>
          {pageType === "CHAT" && chatRoomStatus === "MATCHED" && documentId && (
            <Link
              to={`/document/${documentId}`}
              className="flex justify-center items-center rounded-[6px] h-12 body-l-semibold bg-violet-50-transpar cursor-pointer"
            >
              {t("chatRoom.visaApplicationLink")}
            </Link>
          )}
        </div>
      );

    case "REJECTED":
      return (
        <div
          className={`flex flex-col gap-5 p-6 border-[1.5px] border-green-100 text-green-800 rounded-[12px] bg-gray-0 ${size}`}
        >
          <div className="flex flex-row items-center gap-4">
            <IcMessageBoxCross color="var(--green-800)" />
            <div className="flex flex-col gap-[1px]">
              <div className="body-l-bold">{t("chatRoom.retainerDeclinedLine1")}</div>
              <div className="body-l-bold">{t("chatRoom.retainerDeclinedLine2")}</div>
            </div>
          </div>
        </div>
      );

    case "CANCELED":
      return (
        <div
          className={`flex flex-col gap-5 p-6 border-[1.5px] border-green-100 text-green-800 rounded-[12px] bg-gray-0 ${size}`}
        >
          <div className="flex flex-row items-center gap-4">
            <IcMessageBoxCross color="var(--green-800)" />
            <div className="flex flex-col gap-[1px]">
              <div className="body-l-bold">{t("chatRoom.retainerCanceled")}</div>
              <div className="body-s-medium">{t("chatRoom.agentNoLongerCanWrite", { agentName })}</div>
            </div>
          </div>
        </div>
      );

    case "FEEDBACK_REQUIRED":
      return (
        <div className={`flex flex-col rounded-[12px] border-[1.5px] border-violet-50 overflow-hidden ${size}`}>
          <div className="flex flex-col items-center gap-5 p-6 bg-gray-0 text-text-base">
            <div className="flex flex-col gap-2 body-l-bold">{t("chatRoom.feedbackRequired")}</div>
            {!isSentByMe &&
              !feedbackSubmitted &&
              chatRoomStatus !== "CHATROOM_BLOCKED" &&
              userType === "FILLED_FOREIGNER" && (
                <button
                  className="body-s-semibold rounded-[6px] h-[48px] bg-gray-100 text-text-base cursor-pointer w-full"
                  onClick={() => {
                    // 5-1: 뱃지리뷰 작성 + 행정사 종료 → ServiceReviewModal(2)
                    // 5-2: 뱃지리뷰 작성 + 행정사 미종료 → VisaResponseModal(3)
                    // 5-3: 뱃지리뷰 미작성 + 행정사 종료 → BadgeReviewModal(1)
                    // 5-4: 뱃지리뷰 미작성 + 행정사 미종료 → VisaResponseModal(3)
                    const isBadgeReviewDone = reviewProgress?.isReview; // isReview = 뱃지 리뷰 여부

                    if (proposalEndRequired) {
                      // 행정사가 수임을 종료하지 않은 경우: VisaResponseModal(3)
                      reviewHandler(3);
                    } else {
                      // 행정사가 수임을 종료한 경우
                      if (isBadgeReviewDone) {
                        // 뱃지리뷰 작성됨 → ServiceReviewModal(2)
                        reviewHandler(2);
                      } else {
                        // 뱃지리뷰 미작성 → BadgeReviewModal(1)
                        reviewHandler(1);
                      }
                    }
                    // isFeedbackRequired 상태 업데이트 (FEEDBACK_REQUIRED에서 호출됨)
                    showReviewModal?.(true, true);
                  }}
                >
                  {t("chatRoom.writeFeedback")}
                </button>
              )}
          </div>
        </div>
      );
  }
};

export default ChatSystemMessage;

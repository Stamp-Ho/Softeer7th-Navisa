import { useForeignerProgressQuery } from "../../../../api/queries/useForeignerProgressQuery";
import BadgeReviewModal from "./BadgeReviewModal";
import ServiceReviewModal from "./ServiceReviewModal";
import VisaResponseModal from "./VisaResponseModal";

type ReviewModalProps = {
  reviewHandler: (num: number) => void;
  modalView: number;
  agentId: string;
  setIsReviewRequired: (b: boolean) => void;
  formId: string;
  isAgent: boolean;
  matchingEndRequired?: boolean;
  isFeedbackRequired?: boolean; // FEEDBACK_REQUIRED 메시지에서 띄워졌는지
};

const ReviewModal = ({
  reviewHandler,
  modalView,
  agentId,
  setIsReviewRequired,
  formId,
  isAgent,
  matchingEndRequired,
  isFeedbackRequired = false,
}: ReviewModalProps) => {
  const { data: reviewProgress } = useForeignerProgressQuery({
    type: isFeedbackRequired ? "FEEDBACK_REQUIRED" : undefined,
  });
  return (
    <>
      {modalView === 1 && (
        <BadgeReviewModal
          reviewHandler={reviewHandler}
          agentId={agentId}
          setIsReviewRequired={setIsReviewRequired}
          reviewProgress={reviewProgress}
          matchingEndRequired={matchingEndRequired}
          shouldGoToServiceReview={isFeedbackRequired}
        />
      )}
      {modalView === 2 && <ServiceReviewModal reviewHandler={reviewHandler} />}
      {modalView === 3 && (
        <VisaResponseModal
          reviewHandler={reviewHandler}
          formId={formId}
          isAgent={isAgent}
          reviewProgress={reviewProgress}
        />
      )}
    </>
  );
};

export default ReviewModal;

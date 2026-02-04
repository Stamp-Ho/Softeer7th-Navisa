import BadgeReviewModal from "./BadgeReviewModal";
import ServiceReviewModal from "./ServiceReviewModal";

type ReviewModalProps = {
  reviewHandler: (num: number) => void;
  modalView: number;
};

const ReviewModal = ({ reviewHandler, modalView }: ReviewModalProps) => {
  return (
    <>
      {modalView === 1 && <BadgeReviewModal reviewHandler={reviewHandler} />}
      {modalView === 2 && <ServiceReviewModal reviewHandler={reviewHandler} />}
    </>
  );
};

export default ReviewModal;

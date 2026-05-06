import { useState, useRef, useEffect } from "react";
import Button from "../../../../components/common/Button";
import Modal from "../../../../components/common/Modal";
import { useTranslation } from "react-i18next";
import { useFeedbackReviewMutation } from "../../../../api/mutations/useReviewMutation";

type ServiceReviewModalParams = {
  reviewHandler: (num: number) => void;
  setFeedbackSubmitted: (value: boolean) => void;
};

const ServiceReviewModal = ({ reviewHandler, setFeedbackSubmitted }: ServiceReviewModalParams) => {
  const modalRef = useRef<HTMLDivElement>(null);
  const { t } = useTranslation(["components"]);
  const { mutate: submitFeedback } = useFeedbackReviewMutation(); // 리뷰 제출 API 훅
  const [reviewText, setReviewText] = useState<string>("");
  const MAX_LENGTH = 1000;

  useEffect(() => {
    const modal = modalRef.current;
    if (!modal) return;

    // 모달이 마운트될 때 첫 번째 포커스 가능한 요소로 포커스 이동
    const focusableElements = modal.querySelectorAll(
      'a, button, input, textarea, select, [tabindex]:not([tabindex="-1"])',
    );
    const firstElement = focusableElements[1] as HTMLElement;
    if (firstElement) {
      firstElement.focus();
    }
  }, []);

  const handleSubmit = () => {
    submitFeedback(
      { content: reviewText },
      {
        onSuccess: () => {
          setFeedbackSubmitted(true); // 피드백 제출 완료 표시
          reviewHandler(0);
        },
        onError: () => {
          console.error(t("review.reviewFailed"));
        },
      },
    );
  };

  return (
    <Modal ref={modalRef} onClose={() => reviewHandler(0)}>
      <div className="flex flex-col px-5 pt-4">
        <div className="title-l-semibold text-text-base">{t("review.serviceDescription")}</div>
        <div className="mt-5 bg-gray-50 rounded-lg p-4 h-124.25 flex flex-col">
          <textarea
            className="w-full flex-1 resize-none outline-none placeholder:text-text-sub"
            placeholder={t("review.servicePlaceholder")}
            value={reviewText}
            onChange={(e) => {
              const value = e.target.value;

              // 1,000자 제한
              if (value.length <= MAX_LENGTH) {
                setReviewText(value);
              }
            }}
            maxLength={MAX_LENGTH}
            tabIndex={1}
          />

          {/* 글자 수 카운터 */}
          <div className="text-right body-s-semibold text-text-sub mt-2">
            {reviewText.length}/{MAX_LENGTH}
          </div>
        </div>
        <Button
          variant="primary"
          size="large"
          className="w-full mt-10 mb-9.75"
          disabled={false}
          onClick={() => handleSubmit()}
          tabIndex={1}
        >
          {t("review.next")}
        </Button>
      </div>
    </Modal>
  );
};

export default ServiceReviewModal;

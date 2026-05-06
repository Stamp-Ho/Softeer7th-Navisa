import {
  useAgentStatusFinishedMutation,
  useForeignerStatusFinishedMutation,
} from "../../../../api/mutations/useReviewMutation";
import type { ForeignerProgressResponse } from "../../../../api/types/foreigner";
import Button from "../../../../components/common/Button";
import Modal from "../../../../components/common/Modal";
import { useRef, useEffect } from "react";
import { useTranslation } from "react-i18next";

type VisaResponseModalProps = {
  reviewHandler: (num: number) => void;
  formId: string;
  isAgent: boolean;
  reviewProgress?: ForeignerProgressResponse; // 수임자의 진행 상황 (리뷰 작성 여부) - 정확한 타입이 필요할 수 있음
};

const VisaResponseModal = ({ reviewHandler, formId, isAgent, reviewProgress }: VisaResponseModalProps) => {
  const { t } = useTranslation(["components"]);
  const modalRef = useRef<HTMLDivElement>(null);
  const { mutate: agentFinishStatus, isPending: isAgentPending } = useAgentStatusFinishedMutation(formId);
  const { mutate: foreignerFinishStatus, isPending: isForeignerPending } = useForeignerStatusFinishedMutation();

  useEffect(() => {
    const modal = modalRef.current;
    if (!modal) return;

    // 모달이 마운트될 때 첫 번째 포커스 가능한 요소로 포커스 이동
    const focusableElements = modal.querySelectorAll(
      'a, button, input, textarea, select, [tabindex]:not([tabindex="-1"])',
    );
    const firstElement = focusableElements[0] as HTMLElement;
    if (firstElement) {
      firstElement.focus();
    }
  }, []);
  return (
    <Modal ref={modalRef} onClose={() => reviewHandler(0)}>
      <div className="flex flex-col gap-8 items-center w-full">
        <div className="flex flex-col gap-2 items-center title-l-semibold text-text-base">
          {t("chatRoom.finishRetainerQuestion")}
        </div>
        <div className="flex flex-row justify-between w-full gap-4">
          <Button
            variant="gray"
            size="large"
            className="w-full"
            onClick={() => {
              reviewHandler(0);
            }}
            tabIndex={1}
          >
            {t("button.no")}
          </Button>
          <Button
            variant="primary"
            size="large"
            className="w-full"
            disabled={isAgentPending || isForeignerPending}
            tabIndex={1}
            onClick={() => {
              if (isAgent) {
                agentFinishStatus(
                  { isFinished: true },
                  {
                    onSuccess: () => reviewHandler(0),
                    onError: () => {
                      console.error(t("chatRoom.finishRetainerError"));
                    },
                  },
                );
              } else {
                foreignerFinishStatus(undefined, {
                  onSuccess: () => {
                    if (!reviewProgress?.isReview) reviewHandler(1);
                    else reviewHandler(2);
                  },
                  onError: () => {
                    console.error(t("chatRoom.finishRetainerError"));
                  },
                });
              }
            }}
          >
            {t("button.yes")}
          </Button>
        </div>
      </div>
    </Modal>
  );
};

export default VisaResponseModal;

import {
  useAgentStatusFinishedMutation,
  useForeignerStatusFinishedMutation,
} from "../../../../api/mutations/useReviewMutation";
import type { ForeignerProgressResponse } from "../../../../api/types/foreigner";
import Button from "../../../../components/common/Button";
import Modal from "../../../../components/common/Modal";

type VisaResponseModalProps = {
  reviewHandler: (num: number) => void;
  formId: string;
  isAgent: boolean;
  reviewProgress?: ForeignerProgressResponse; // 수임자의 진행 상황 (리뷰 작성 여부) - 정확한 타입이 필요할 수 있음
};

const VisaResponseModal = ({
  reviewHandler,
  formId,
  isAgent,
  reviewProgress,
}: VisaResponseModalProps) => {
  const { mutate: agentFinishStatus, isPending: isAgentPending } =
    useAgentStatusFinishedMutation(formId);
  const { mutate: foreignerFinishStatus, isPending: isForeignerPending } =
    useForeignerStatusFinishedMutation();
  return (
    <Modal onClose={() => reviewHandler(0)}>
      <div className="flex flex-col gap-8 items-center w-full">
        <div className="flex flex-col gap-2 items-center title-l-semibold text-text-base">
          수임을 종료하시겠습니까?
        </div>
        <div className="flex flex-row justify-between w-full gap-4">
          <Button
            variant="gray"
            size="large"
            className="w-full"
            onClick={() => {
              reviewHandler(0);
            }}
          >
            아니오
          </Button>
          <Button
            variant="primary"
            size="large"
            className="w-full"
            disabled={isAgentPending || isForeignerPending}
            onClick={() => {
              if (isAgent) {
                agentFinishStatus(
                  { isFinished: true },
                  {
                    onSuccess: () => reviewHandler(0),
                    onError: () => {
                      console.error("수임 종료 처리 실패");
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
                    console.error("수임 종료 처리 실패");
                  },
                });
              }
            }}
          >
            네
          </Button>
        </div>
      </div>
    </Modal>
  );
};

export default VisaResponseModal;

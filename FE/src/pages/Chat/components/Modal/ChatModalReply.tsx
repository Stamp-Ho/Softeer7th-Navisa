import { useTranslation } from "react-i18next";
import {
  usePostProposalAccepted,
  usePostProposalRejected,
} from "../../../../api/mutations/useMatchingMutation";
import Button from "../../../../components/common/Button";

type ProposalParams = {
  onAnswer: (num: number) => void;
  roomId: number;
};

const ChatModalReply = ({ onAnswer, roomId }: ProposalParams) => {
  const { t } = useTranslation(["components"]);
  const { mutate: sendProposalRejected } = usePostProposalRejected(roomId);
  const { mutate: sendProposalAccepted } = usePostProposalAccepted(roomId);

  return (
    <div className="flex flex-col gap-8 items-center w-full">
      <div className="flex flex-col gap-2 items-center">
        <div className="title-l-semibold text-text-base">
          {t("chatModal.acceptProposalTitle")}
        </div>
        <div className="body-l-medium text-text-base">
          {t("chatModal.acceptProposalDesc")}
        </div>
      </div>
      <div className="flex flex-row justify-between w-full gap-4">
        <Button
          variant="gray"
          size="large"
          className="w-full"
          onClick={() => {
            sendProposalRejected();
            onAnswer(0);
          }}
        >
          {t("chatModal.reject")}
        </Button>
        <Button
          variant="primary"
          size="large"
          className="w-full"
          onClick={() => {
            sendProposalAccepted();
            onAnswer(0);
          }}
        >
          {t("chatModal.accept")}
        </Button>
      </div>
    </div>
  );
};

export default ChatModalReply;

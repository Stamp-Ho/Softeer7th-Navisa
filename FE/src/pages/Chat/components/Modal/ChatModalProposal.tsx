import { useTranslation } from "react-i18next";
import { usePostProposal } from "../../../../api/mutations/useMatchingMutation";
import Button from "../../../../components/common/Button";

type ProposalParams = {
  onAnswer: (num: number) => void;
  roomId: number;
};

const ChatModalProposal = ({ onAnswer, roomId }: ProposalParams) => {
  const { t } = useTranslation(["components"]);
  const { mutate: sendProposal } = usePostProposal(roomId);

  return (
    <div className="flex flex-col gap-8 items-center w-full">
      <div className="flex flex-col gap-2 items-center">
        <div className="title-l-semibold text-text-base">
          {t("chatModal.proposeRetainerTitle")}
        </div>
        <div className="body-l-medium text-text-base">
          {t("chatModal.proposeRetainerDesc")}
        </div>
      </div>
      <Button
        variant="primary"
        size="large"
        className="w-full"
        onClick={() => {
          sendProposal();
          onAnswer(0);
        }}
      >
        {t("chatModal.propose")}
      </Button>
    </div>
  );
};

export default ChatModalProposal;

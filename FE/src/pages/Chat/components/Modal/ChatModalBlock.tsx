import { useTranslation } from "react-i18next";
import { usePostBlocked } from "../../../../api/mutations/useMatchingMutation";
import Button from "../../../../components/common/Button";

type ProposalParams = {
  onAnswer: (num: number) => void;
  roomId: number;
};

const ChatModalBlock = ({ onAnswer, roomId }: ProposalParams) => {
  const { t } = useTranslation(["components"]);
  const { mutate: blockUser } = usePostBlocked(roomId);

  return (
    <div className="flex flex-col gap-8 items-center w-full">
      <div className="flex flex-col gap-2 items-center">
        <div className="title-l-semibold text-text-base">
          {t("chatModal.blockUserTitle")}
        </div>
        <div className="body-l-medium text-text-base">
          {t("chatModal.blockUserDesc")}
        </div>
      </div>
      <Button
        variant="primary"
        size="large"
        className="w-full"
        onClick={() => {
          blockUser(undefined, {
            onSuccess: () => onAnswer(0),
            onError: () => {
              console.log(t("chatModal.blockFailed"));
            },
          });
        }}
      >
        {t("chatModal.blockButton")}
      </Button>
    </div>
  );
};

export default ChatModalBlock;

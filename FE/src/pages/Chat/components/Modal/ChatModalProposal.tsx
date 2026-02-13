import { usePostProposal } from "../../../../api/hooks/useMatchingMutation";
import Button from "../../../../components/common/Button";

type ProposalParams = {
  onAnswer: (num: number) => void;
  roomId: number;
};

const ChatModalProposal = ({ onAnswer, roomId }: ProposalParams) => {
  const { mutate: sendProposal } = usePostProposal(roomId);

  return (
    <div className="flex flex-col gap-8 items-center w-full">
      <div className="flex flex-col gap-2 items-center">
        <div className="title-l-semibold text-text-base">
          수임을 제안하시겠습니까?
        </div>
        <div className="body-l-medium text-text-base">
          상대방이 수락하면 수임이 확정돼요.
        </div>
      </div>
      <Button
        type="primary"
        size="large"
        className="w-full"
        onClick={() => {
          sendProposal();
          onAnswer(0);
        }}
      >
        제안하기
      </Button>
    </div>
  );
};

export default ChatModalProposal;

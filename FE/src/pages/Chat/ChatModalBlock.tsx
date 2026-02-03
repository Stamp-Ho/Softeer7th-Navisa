import Button from "../../components/common/Button";

type ProposalParams = {
  onAnswer: (num: number) => void;
};

const ChatModalBlock = ({ onAnswer }: ProposalParams) => {
  return (
    <div className="flex flex-col gap-8 items-center w-full">
      <div className="flex flex-col gap-2 items-center">
        <div className="title-l-semibold text-text-base">
          해당 사용자를 차단하시겠습니까?
        </div>
        <div className="body-l-medium text-text-base">
          언제든 상담 메시지 창에서 차단을 해제할 수 있어요.
        </div>
      </div>
      <Button
        type="primary"
        size="large"
        className="w-full"
        onClick={() => onAnswer(0)}
      >
        차단하기
      </Button>
    </div>
  );
};

export default ChatModalBlock;

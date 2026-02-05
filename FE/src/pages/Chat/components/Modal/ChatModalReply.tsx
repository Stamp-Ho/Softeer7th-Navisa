import Button from "../../../../components/common/Button";

type ProposalParams = {
  onAnswer: (num: number) => void;
};

const ChatModalReply = ({ onAnswer }: ProposalParams) => {
  return (
    <div className="flex flex-col gap-8 items-center w-full">
      <div className="flex flex-col gap-2 items-center">
        <div className="title-l-semibold text-text-base">
          수임 제안을 수락하시겠습니까?
        </div>
        <div className="body-l-medium text-text-base">
          수락 시, 행정사와 함께 비자 신청서를 작성할 수 있어요.
        </div>
      </div>
      <div className="flex flex-row justify-between w-full gap-4">
        <Button
          type="gray"
          size="large"
          className="w-full"
          onClick={() => onAnswer(0)}
        >
          거절하기
        </Button>
        <Button
          type="primary"
          size="large"
          className="w-full"
          onClick={() => onAnswer(0)}
        >
          수락하기
        </Button>
      </div>
    </div>
  );
};

export default ChatModalReply;

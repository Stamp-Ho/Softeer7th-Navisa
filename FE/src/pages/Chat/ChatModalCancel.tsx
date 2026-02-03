import Button from "../../components/common/Button";

type ProposalParams = {
  onAnswer: (num: number) => void;
};

const ChatModalCancel = ({ onAnswer }: ProposalParams) => {
  return (
    <div className="flex flex-col gap-8 items-center w-full">
      <div className="flex flex-col gap-2 items-center">
        <div className="title-l-semibold text-text-base">
          수임을 취소하시겠습니까?
        </div>
        <div className="body-l-medium text-text-base">
          취소 시, 행정사의 비자 신청서 작성 권한이 사라져요.
        </div>
      </div>
      <Button
        type="primary"
        size="large"
        className="w-full"
        onClick={() => onAnswer(0)}
      >
        취소하기
      </Button>
    </div>
  );
};

export default ChatModalCancel;

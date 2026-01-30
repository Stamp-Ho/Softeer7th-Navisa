import { useState } from "react";
import Button from "../../../components/common/Button";
import Modal from "../../../components/common/Modal";
import TextInput from "../../../components/common/TextInput";

type ChatActivateProps = {
  onClose: () => void;
  onSendSuccess: () => void;
  isAgent: boolean;
};

const ChatActivateModal = ({
  onClose,
  onSendSuccess,
  isAgent,
}: ChatActivateProps) => {
  const [firstMessage, setFirstMessage] = useState<string>("");

  const handleSendMessage = () => {
    onSendSuccess(); // 성공 처리 (toast)
    onClose(); // UI 닫기
  };

  return (
    <Modal
      className="flex flex-col items-center px-5 pt-4 pb-6.25"
      onClose={onClose}
    >
      <div className="title-l-semibold text-text-base">
        해당 {isAgent ? "의뢰인" : "행정사"}에게 상담 메시지를 전송할까요?
      </div>
      <div className="body-l-medium text-text-base mt-3 mb-8.5">
        {isAgent ? "의뢰인으로부터" : "행정사로부터"} 답장이 오면 상담
        메시지함에서 확인할 수 있어요.
      </div>
      <TextInput
        type="text"
        className="placeholder:text-text-base mb-5"
        placeholder={
          isAgent
            ? "의뢰인님, 도움을 드리고 싶어요!"
            : "행정사님, 상담하고 싶어요!"
        }
        value={firstMessage}
        setValue={setFirstMessage}
      />
      <Button
        type="primary"
        size="large"
        className="w-full"
        onClick={handleSendMessage}
      >
        메시지 보내기
      </Button>
    </Modal>
  );
};

export default ChatActivateModal;

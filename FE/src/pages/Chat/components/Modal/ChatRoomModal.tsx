import Modal from "../../../../components/common/Modal";
import ChatModalBlock from "./ChatModalBlock";
import ChatModalCancel from "./ChatModalCancel";
import ChatModalProposal from "./ChatModalProposal";
import ChatModalReply from "./ChatModalReply";

type ChatRoomModalProps = {
  onModalAction: (num: number) => void;
  modalView: number;
};

const ChatRoomModal = ({ onModalAction, modalView }: ChatRoomModalProps) => {
  return (
    <Modal
      className="flex flex-col items-center px-5 pt-4 pb-6.25"
      onClose={() => onModalAction(0)}
    >
      {modalView === 1 && <ChatModalProposal onAnswer={onModalAction} />}
      {modalView === 2 && <ChatModalCancel onAnswer={onModalAction} />}
      {modalView === 3 && <ChatModalReply onAnswer={onModalAction} />}
      {modalView === 4 && <ChatModalBlock onAnswer={onModalAction} />}
    </Modal>
  );
};

export default ChatRoomModal;

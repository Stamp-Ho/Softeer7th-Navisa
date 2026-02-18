import Modal from "../../../../components/common/Modal";
import ChatModalBlock from "./ChatModalBlock";
import ChatModalCancel from "./ChatModalCancel";
import ChatModalProposal from "./ChatModalProposal";
import ChatModalReply from "./ChatModalReply";

type ChatRoomModalProps = {
  onModalAction: (num: number) => void;
  modalView: number;
  roomId: number;
};

const ChatRoomModal = ({
  onModalAction,
  modalView,
  roomId,
}: ChatRoomModalProps) => {
  return (
    <Modal
      className="flex flex-col items-center px-5 pt-4 pb-6.25"
      onClose={() => onModalAction(0)}
    >
      {modalView === 1 && (
        <ChatModalProposal onAnswer={onModalAction} roomId={roomId} />
      )}
      {modalView === 2 && (
        <ChatModalCancel onAnswer={onModalAction} roomId={roomId} />
      )}
      {modalView === 3 && (
        <ChatModalReply onAnswer={onModalAction} roomId={roomId} />
      )}
      {modalView === 4 && (
        <ChatModalBlock onAnswer={onModalAction} roomId={roomId} />
      )}
    </Modal>
  );
};

export default ChatRoomModal;

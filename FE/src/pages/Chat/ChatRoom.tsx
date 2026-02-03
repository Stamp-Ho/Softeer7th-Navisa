import ChatBody from "./ChatBody";
import ChatRoomFooter from "./ChatRoomFooter";
import ChatRoomHeader from "./ChatRoomHeader";

type ChatRoomParams = {
  amAgent: boolean;
  chatRoomId: number | null;
  onClose: () => void;
  onModalAction: (num: number) => void;
};

const isMatched = true;

const ChatRoom = ({
  amAgent,
  // chatRoomId,
  onClose,
  onModalAction,
}: ChatRoomParams) => {
  return (
    <>
      <ChatRoomHeader
        amAgent={amAgent}
        isMatched={isMatched}
        onClose={onClose}
        onModalAction={onModalAction}
      />
      <ChatBody onModalAction={onModalAction} />
      <ChatRoomFooter isMatched={isMatched} />
    </>
  );
};

export default ChatRoom;

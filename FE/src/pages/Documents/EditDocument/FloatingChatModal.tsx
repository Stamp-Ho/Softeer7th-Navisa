import ChatRoom from "../../Chat/components/ChatRoom/ChatRoom";

type FloatingChatModalProps = {
  onClose: () => void;
  chatRoomId: number;
};

const FloatingChatModal = ({ onClose, chatRoomId }: FloatingChatModalProps) => {
  return (
    <div
      role="dialog"
      aria-modal="true"
      aria-label="채팅방"
      className="flex flex-col justify-between w-150 h-200 rounded-[20px] border border-violet-100 bg-background-default shadow overflow-hidden absolute right-23 bottom-3"
    >
      <ChatRoom
        chatRoomId={chatRoomId}
        onClose={onClose}
        onModalAction={() => {}}
        profileImg={null}
        pageType="DOCUMENT"
      />
    </div>
  );
};

export default FloatingChatModal;

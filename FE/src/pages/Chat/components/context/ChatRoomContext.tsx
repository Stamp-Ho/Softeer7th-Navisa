import { createContext, useContext } from "react";
import { useChatRoom } from "../hooks/useChatRoom";

const ChatRoomContext = createContext<ReturnType<typeof useChatRoom> | null>(
  null,
);

export const ChatRoomProvider = ({
  chatRoomId,
  children,
  isActive = false,
  initialRoomStatus,
}: {
  chatRoomId: number;
  children: React.ReactNode;
  isActive?: boolean;
  initialRoomStatus?: string;
}) => {
  // isActive prop에 따라 읽음 처리(READ) 기능 활성화 여부 결정
  const chatValue = useChatRoom(chatRoomId, isActive, initialRoomStatus);

  return (
    <ChatRoomContext.Provider value={chatValue}>
      {children}
    </ChatRoomContext.Provider>
  );
};

export const useChatRoomContext = () => {
  const context = useContext(ChatRoomContext);
  if (!context)
    throw new Error(
      "useChatRoomContext must be used within a ChatRoomProvider",
    );
  return context;
};

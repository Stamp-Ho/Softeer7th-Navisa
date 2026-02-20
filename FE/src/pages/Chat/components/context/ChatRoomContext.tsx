import { createContext, useContext } from "react";
import { useChatRoom } from "../hooks/useChatRoom";

const ChatRoomContext = createContext<ReturnType<typeof useChatRoom> | null>(
  null,
);

export const ChatRoomProvider = ({
  chatRoomId,
  children,
}: {
  chatRoomId: number;
  children: React.ReactNode;
}) => {
  // 여기서 'true'를 전달해 모든 로직(데이터 fetching 포함)을 활성화합니다.
  const chatValue = useChatRoom(chatRoomId, true);

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

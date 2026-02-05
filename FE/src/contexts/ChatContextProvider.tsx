import { useState } from "react";
import { ChatContext } from "./ChatContext";

export const ChatContextProvider = ({
  children,
}: {
  children: React.ReactNode;
}) => {
  const [isChatUnread, setIsChatUnread] = useState<number>(0);

  return (
    <ChatContext.Provider value={{ isChatUnread, setIsChatUnread }}>
      {children}
    </ChatContext.Provider>
  );
};

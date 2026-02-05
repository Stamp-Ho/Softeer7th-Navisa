import { createContext } from "react";

type ChatContextType = {
  isChatUnread: number;
  setIsChatUnread: (a: number) => void;
};

export const ChatContext = createContext<ChatContextType | undefined>(
  undefined,
);

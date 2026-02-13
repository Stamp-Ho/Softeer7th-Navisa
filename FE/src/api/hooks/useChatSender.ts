import { v4 as uuidv4 } from "uuid";
import { useWebSocket } from "../../contexts/WebSocketContext";
import type { Send } from "../websocket/types";

export const useChatSender = () => {
  const { sendMessage } = useWebSocket();

  const sendChat = (
    roomId: number,
    type:
      | "TEXT"
      | "PROPOSAL"
      | "ACCEPTED"
      | "REJECTED"
      | "CANCELED"
      | "SYSTEM"
      | "READ",
    content: string = "",
  ) => {
    const payload: Send = {
      roomId,
      clientMessageId: uuidv4(), // echo 처리용
      content,
      type,
      sentAt: new Date().toISOString(),
    };

    sendMessage(payload);
  };

  return { sendChat };
};

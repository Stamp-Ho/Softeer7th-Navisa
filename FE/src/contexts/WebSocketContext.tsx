import { createContext, useContext, useEffect, useState } from "react";
import {
  connectWebSocket,
  disconnectWebSocket,
  sendMessageToServer,
} from "../api/websocket/stompClient";
import type { Message, Send } from "../api/websocket/types";
import { useAuth } from "./AuthContextProvider";

type WebSocketContextType = {
  messages: Message[];
  sendMessage: (payload: Send) => void;
  isConnected: boolean;
};

const WebSocketContext = createContext<WebSocketContextType | null>(null);
export const WebSocketProvider = ({
  children,
}: {
  children: React.ReactNode;
}) => {
  const [messages, setMessages] = useState<Message[]>([]);
  const { accessToken, userId } = useAuth();
  const [isConnected, setIsConnected] = useState(false);

  useEffect(() => {
    if (!accessToken) return;

    connectWebSocket(
      accessToken,
      (msg: Message) => {
        setMessages((prev) => {
          // READ 수신 처리(읽음처리)
          if (msg.type === "READ") {
            return prev.map((m) => {
              if (
                m.roomId === msg.roomId &&
                m.senderId === userId // 내가 보낸 메시지
              ) {
                return { ...m, isRead: true };
              }
              return m;
            });
          }

          // 일반 메시지 수신
          return [...prev, { ...msg, isRead: false }];
        });
      },
      (connected: boolean) => {
        setIsConnected(connected);
      },
    );

    return () => {
      disconnectWebSocket();
    };
  }, [accessToken]);

  const handleSendMessage = (payload: Send) => {
    sendMessageToServer(payload);
  };
  return (
    <WebSocketContext.Provider
      value={{ messages, sendMessage: handleSendMessage, isConnected }}
    >
      {children}
    </WebSocketContext.Provider>
  );
};

export const useWebSocket = () => {
  const context = useContext(WebSocketContext);
  if (!context) {
    throw new Error("useWebSocket must be used within WebSocketProvider");
  }
  return context;
};

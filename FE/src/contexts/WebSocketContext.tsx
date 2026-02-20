import React, {
  createContext,
  useContext,
  useEffect,
  useState,
  useRef,
  useCallback,
} from "react";
import {
  connectWebSocket,
  disconnectWebSocket,
  sendMessageToServer,
  isStompConnected,
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
  const isConnectedRef = useRef(false);
  const userIdRef = useRef(userId);

  // userId 최신값 유지
  useEffect(() => {
    userIdRef.current = userId;
  }, [userId]);

  useEffect(() => {
    // 토큰이 없으면 연결 시도 X
    if (!accessToken) return;

    // 연결 시작
    connectWebSocket(
      accessToken,
      (msg: Message) => {
        setMessages((prev) => {
          if (msg.type === "READ") {
            return prev.map((m) => {
              if (
                userIdRef.current &&
                m.roomId === msg.roomId &&
                m.senderId === userIdRef.current
              ) {
                return { ...m, isRead: true };
              }
              return m;
            });
          }
          return [...prev, { ...msg, isRead: false }];
        });
      },
      (connected: boolean) => {
        // 상태 업데이트
        setIsConnected(connected);
        isConnectedRef.current = connected;
      },
    );

    // Cleanup: 컴포넌트 언마운트 시 연결 해제
    return () => {
      disconnectWebSocket();
      setIsConnected(false);
      isConnectedRef.current = false;
    };
  }, [accessToken]); // accessToken이 변경될 때만 재실행

  const handleSendMessage = useCallback((payload: Send) => {
    // 1. Context 상태 확인
    if (!isConnectedRef.current) {
      console.warn("Context says disconnected. Cannot send message.");
      return;
    }

    // 2. 실제 클라이언트 연결 상태 확인
    if (!isStompConnected()) {
      console.warn(
        "Actual STOMP client is disconnected. Attempting to reconnect or waiting...",
      );
      return;
    }

    sendMessageToServer(payload);
  }, []);

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

import { Client, type IMessage } from "@stomp/stompjs";
import type { Send, Message } from "./types";

// 싱글톤 인스턴스
let stompClient: Client | null = null;
let currentToken: string | null = null;

export const connectWebSocket = (
  token: string,
  onMessage: (msg: Message) => void,
  onConnectionChange?: (connected: boolean) => void,
) => {
  // 이미 연결된 클라이언트가 있는데 토큰이 변경되었다면 기존 연결 종료
  if (stompClient && currentToken !== token) {
    console.log("Token changed, disconnecting existing WebSocket...");
    disconnectWebSocket();
  }

  // 이미 연결된 클라이언트가 있고 토큰도 같으면 재사용
  if (stompClient && stompClient.active && currentToken === token) {
    console.log("Reusing existing WebSocket connection");
    onConnectionChange?.(true);
    return stompClient;
  }

  const client = new Client({
    webSocketFactory: () => new WebSocket("wss://api.navisa.site/ws"),
    connectHeaders: { Authorization: `Bearer ${token}` },
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,

    onConnect: () => {
      console.log("WebSocket Connected");
      currentToken = token; // 연결 성공 시 토큰 기록
      onConnectionChange?.(true);

      client.subscribe("/user/chat/subscribe", (frame: IMessage) => {
        try {
          const message: Message = JSON.parse(frame.body);
          onMessage(message);
        } catch (e) {
          console.error("Failed to parse message:", e);
        }
      });
    },

    onStompError: (frame) => {
      console.error("STOMP Error:", frame);
      // 401 에러인 경우 재연결을 중단하고 연결 해제
      if (frame.body && frame.body.includes("401")) {
        console.error("Unauthorized WebSocket connection. Deactivating...");
        onConnectionChange?.(false);
        // 전역 변수 초기화 및 비활성화 동시 처리
        disconnectWebSocket();
      }
    },

    onWebSocketClose: () => {
      console.warn("WebSocket closed");
      // currentToken은 disconnectWebSocket 내에서만 초기화
      // 의도하지 않은 close(네트워크 끊김)일 때 STOMP 자동 재연결을 방해하지 않기 위함
      onConnectionChange?.(false);
    },
  });

  stompClient = client;
  stompClient.activate();

  return stompClient;
};

export const disconnectWebSocket = () => {
  // 1. 현재 클라이언트를 임시 변수에 담음
  const clientToDeactivate = stompClient;

  // 2. 전역 변수는 즉시 null로 초기화
  stompClient = null;
  currentToken = null;

  // 3. 실제 연결 해제는 비동기로 진행
  if (clientToDeactivate) {
    clientToDeactivate
      .deactivate()
      .then(() => {
        console.log("WebSocket Disconnected");
      })
      .catch((err) => {
        console.error("Error during deactivation:", err);
      });
  }
};

export const sendMessageToServer = (payload: Send) => {
  if (!stompClient) {
    console.error("Stomp Client is null. Connection might be closed.");
    return;
  }

  if (!stompClient.connected) {
    console.warn("Stomp client exists but not connected yet.");
    return;
  }

  const destination =
    payload.type === "READ" ? "/pub/room/message/read" : "/pub/chat/message";

  stompClient.publish({
    destination,
    body: JSON.stringify(payload),
  });
};

// 현재 연결 상태 확인용
export const isStompConnected = () => {
  return stompClient?.connected ?? false;
};

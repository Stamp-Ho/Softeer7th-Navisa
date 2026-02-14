import { Client } from "@stomp/stompjs";
// import SockJS from "sockjs-client";
import type { Send, Message } from "./types";

let stompClient: Client | null = null;

export const connectWebSocket = (
  token: string,
  onMessage: (msg: Message) => void,
  onConnectionChange?: (connected: boolean) => void,
) => {
  if (stompClient?.active) return stompClient;

  stompClient = new Client({
    webSocketFactory: () => new WebSocket("wss://api.navisa.site/ws"),
    // webSocketFactory: () => new WebSocket("ws://121.172.219.115:15533/ws"),

    connectHeaders: { Authorization: `Bearer ${token}` },

    reconnectDelay: 10000,

    onConnect: () => {
      console.log("WebSocket Connected");
      onConnectionChange?.(true);
      stompClient?.subscribe("/user/chat/subscribe", (frame) => {
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
    },

    onWebSocketClose: () => {
      console.warn("WebSocket closed");
      onConnectionChange?.(false);
    },
  });

  stompClient.activate();
  return stompClient;
};

export const disconnectWebSocket = async () => {
  await stompClient?.deactivate();
  stompClient = null;
};

export const sendMessageToServer = (payload: Send) => {
  if (!stompClient) {
    console.warn("STOMP client is null");
    return;
  }

  if (!stompClient.connected) {
    console.warn("STOMP not connected yet. message skipped.");
    return;
  }

  const destination =
    payload.type === "READ" ? "/pub/room/message/read" : "/pub/chat/message";

  stompClient.publish({
    destination,
    body: JSON.stringify(payload),
  });
};

// export const sendMessageToServer = (payload: Send) => {
//   if (!stompClient || !stompClient.active) {
//     console.error("STOMP Client not connected");
//     return;
//   }
//   const des =
//     payload.type === "READ" ? "/pub/room/message/read" : "/pub/chat/message";

//   stompClient.publish({
//     destination: des,
//     body: JSON.stringify(payload),
//   });
// };

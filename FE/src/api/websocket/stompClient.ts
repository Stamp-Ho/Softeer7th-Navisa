import { Client } from "@stomp/stompjs";
// import SockJS from "sockjs-client";
import type { Send, Message } from "./types";

let stompClient: Client | null = null;

export const connectWebSocket = (
  token: string,
  onMessage: (msg: Message) => void,
) => {
  if (stompClient?.active) return stompClient;

  stompClient = new Client({
    webSocketFactory: () => new WebSocket("wss://api.navisa.site/ws"),
    // webSocketFactory: () => new WebSocket("ws://192.168.1.22:8080/ws"),

    connectHeaders: { Authorization: `Bearer ${token}` },

    reconnectDelay: 10000,

    onConnect: () => {
      console.log("WebSocket Connected");
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
  if (!stompClient || !stompClient.active) {
    console.error("STOMP Client not connected");
    return;
  }
  const des =
    payload.type === "READ" ? "/pub/room/message/read" : "/pub/chat/message";

  stompClient.publish({
    destination: des,
    body: JSON.stringify(payload),
  });
};

export type Send = {
  roomId: number;
  clientMessageId: string; // echo 방식으로 FE 측에서 전송완료된 메세지를 렌더링하기 위함
  senderId?: string;
  content: string;
  type:
    | "TEXT"
    | "PROPOSAL"
    | "ACCEPTED"
    | "REJECTED"
    | "CANCELED"
    | "SYSTEM"
    | "READ"
    | "CHATROOM_BLOCKED";
};

export type Message = {
  messageId: number; // 보조 정렬 기준
  roomId: number;
  clientMessageId: string; // echo 방식으로 FE 측에서 전송완료된 메세지를 렌더링하기 위함.
  senderId?: string;
  receiverId: string; // 수신받는 사용자의 users.id(uuid) for Redis Subscribe 용도
  content: string;
  type:
    | "TEXT"
    | "PROPOSAL"
    | "ACCEPTED"
    | "REJECTED"
    | "CANCELED"
    | "SYSTEM"
    | "READ"
    | "CHATROOM_BLOCKED";
  createdAt: string; // 정렬 기준
  isRead: boolean;
};

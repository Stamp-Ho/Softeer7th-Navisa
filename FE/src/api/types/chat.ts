// 로그인한 사용자의 채팅방 목록을 페이징하여 조회
export interface ChatRoomResponse {
  chatRoomId: number;
  profileImgUrl: string | null;
  opponentName: string;
  roomStatus:
    | "DEFAULT"
    | "PROPOSED"
    | "MATCHED"
    | "REJECTED"
    | "BLOCKED"
    | "CANCELED";
  lastMessage: string;
  noneReadCount: number;
  lastChattedAt: string;
  proposed: boolean;
  proposalMatched: boolean;
}

export type ChatRoomFilter = "all" | "unread" | "matched";

// 특정 채팅방 채팅 기록 메시지
export interface ChatHistoryResponse {
  chatMessageId: number;
  isSentByMe: boolean;
  type:
    | "TEXT"
    | "PROPOSAL"
    | "ACCEPTED"
    | "REJECTED"
    | "CANCELED"
    | "SYSTEM"
    | "READ";
  content: string;
  sentAt: string;
  createdAt: string;
  isRead: boolean;
}

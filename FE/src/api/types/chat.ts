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
    | "CHATROOM_BLOCKED"
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
    | "CHATROOM_BLOCKED"
    | "SYSTEM"
    | "READ";
  content: string;
  sentAt: string;
  createdAt: string;
  isRead: boolean;
}

export interface ChatPageResponse<T> {
  content: T[];
  existsNext: boolean;
  lastElementId: number;
}

// 특정 채팅방 참여자 정보 조회
export interface ChatParticipantsInfo {
  agentInfo: {
    agentId: string;
    top2BadgeIds: number[];
    name: string;
  };
  foreignerInfo: {
    foreignerId: string;
    nickname: string;
    expectedJob: string;
    expectedStartDate: string;
    nationalityIds: number[];
  };
}

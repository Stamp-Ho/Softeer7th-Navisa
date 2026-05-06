// 채팅방 헤더 데이터
export type AgentInfo = {
  agentId: string;
  top2BadgeIds: number[];
  name: string;
};

export type ForeignerInfo = {
  foreignerId: string;
  nickname: string;
  expectedJob: string;
  expectedStartDate: string;
  nationalityIds: number[];
  isReviewRequired: boolean;
};

export type AgentHeaderData = {
  type: "AGENT";
  data?: AgentInfo;
};

export type ForeignerHeaderData = {
  type: "FOREIGNER";
  data?: ForeignerInfo;
};

export type ChatRoomHeaderData = AgentHeaderData | ForeignerHeaderData;

// 특정 채팅방 내역
export type ChatLogData = {
  content: ChatMessage[];
  pageInfo: {
    pageNum: number;
    pageSize: number;
    totalElements: number;
    totalPages: number;
  };
};

export type ChatMessage = {
  messageId: number;
  senderId: string;
  type: string;
  content: string;
  createdAt: string;
  isRead: boolean;
};

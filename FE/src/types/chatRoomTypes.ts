// 채팅방 헤더 데이터
export type AgentInfo = {
  agentInfo: {
    agentId: number;
    name: string;
    profileImageUrl: string;
  };
  strengths: {
    badgeId: number;
    reviewCount: number;
  }[];
};

export type ForeignerInfo = {
  basicInfo: {
    foreignerId: number;
    nickname: string;
    nationIdList: number[];
  };
  expectedInfo: {
    targetJob: string;
    companyName: string;
    startDate: string;
  };
};

export type AgentHeaderData = {
  type: "AGENT";
  data: AgentInfo;
};

export type ForeignerHeaderData = {
  type: "FOREIGNER";
  data: ForeignerInfo;
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

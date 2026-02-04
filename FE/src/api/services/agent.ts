// 행정사 프로필 등록 DTO
export interface RegisterAgentProfileRequest {
  basicInfo: {
    profileImageUrl: string;
    agentName: string;
    birthDate: string; // date string
    officeName: string;
    officeAddress: string;
    officeAddressDetail: string;
    businessTime: string;
  };
  licenseInfo: {
    licenseNo: string;
    licenseIssuedAt: string;
    licenseInnerPageNo: string;
    licenseManagementNo: string;
  };
  detailedInfo: {
    specializedJobCodeIdList: number[];
    availableLanguageIdList: number[];
    agentComment: string;
    additionalHistory: string;
  };
}

// 행정사 상세 조회 응답
export interface GetAgentDetailResponse {
  header: {
    top2badgeIds: number[];
    comment: string;
  };
  expertise: {
    jobCodeIds: number[];
    languageIds: number[];
  };
  agentInfo: {
    agentId: string;
    name: string;
    profileImageUrl: string;
    lastLoginAt: string;
    hasChatRoom: boolean;
    hasBlocked: boolean;
    chatRoomId: number;
  };
  additionalHistory: string;
  reviewSummary: {
    totalCount: number;
    strengths: Array<{ badgeId: number; badgeCount: number }>;
  };
  officeInfo: {
    officeName: string;
    address: string;
    businessHours: string;
    phoneNumber: string;
  };
}

// 행정사 카드 (리스트용)
export interface AgentCardResponse {
  agentId: string;
  agentName: string;
  profileImgUrl: string;
  officeAddress: string;
  agentSpecialityTop2: number[];
  badgeTop2: number[];
  specialityJobCount: number;
}

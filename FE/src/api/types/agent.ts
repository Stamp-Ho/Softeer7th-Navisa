import type { JobType } from "../../types/JobType";
import type { RegionType } from "../../types/RegionType";

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

// 행정사 블로그 응답
export interface AgentRecentFeedbackResponse {
  feedbackId: number;
  feedbackContent: string | null;
  writerId: string;
  writerName: string;
  writerProfileImgUrl: string;
}

// 행정사 상세 조회 응답
export interface GetAgentDetailResponse {
  header: { top2badgeIds: number[]; comment: string };
  expertise: { jobCodeIds: number[]; languageIds: number[] };
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
    strengths: { badgeId: number; badgeCount: number }[];
  };
  officeInfo: {
    officeName: string;
    address: string;
    businessHours: string;
    phoneNumber: string;
  };
}

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
    phoneNumber: string;
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
export interface SliceRequest {
  lastElementId?: string; // UUID, 첫 페이지 조회 시 생략 가능
  size: number;
}

// 행정사 카드 리스트 요청 (쿼리 파라미터)
export interface AgentCardRequest {
  jobGroupNameList?: JobType[];
  regionList?: RegionType[];
  languageIdList?: number[];
  // slice 객체를 평탄화(Flatten)해서 보낼지, 객체로 보낼지는 API 구현 방식에 따라 다르지만
  // 보통 쿼리 스트링에서는 아래와 같이 구성합니다.
  lastElementId?: string | null;
  size: number;
}

// 응답 타입 (Slice 형태일 경우 보통 content 배열과 hasNext 여부를 포함합니다)
export interface AgentCardSliceResponse {
  content: AgentCardResponse[];
  existsNext: boolean;
  lastElementId: string;
}

// 행정사 프로필 상세보기
export interface AgentProfileDetailResponse {
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
    strengths: {
      badgeId: number;
      badgeCount: number;
    }[];
  };
  officeInfo: {
    officeName: string;
    address: string;
    businessHours: string;
    phoneNumber: string;
  };
}

// 뱃지별 행정사 리뷰
export interface AgentBadgeReviewResponse {
  reviewId: number;
  reviewerInitial: string;
  reviewContent: string;
  agentId: string;
  agentName: string;
  agentProfileImgUrl: string;
  badgeTop2: number[];
}

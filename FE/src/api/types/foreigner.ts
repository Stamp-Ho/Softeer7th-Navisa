import type { DegreeLevel } from "./common";

export interface ForeignerRegisterRequest {
  nationIdList: number[];
  languageIdList: number[];
  education: {
    schoolName: string;
    degreeLevel: DegreeLevel;
    majorName: string;
  };
  foreignerCareers: {
    companyName: string;
    jobTitle: string;
    startDate: string;
    endDate: string;
    isWork: boolean;
  }[];
  expectedCompany: {
    companyName: string;
    jobTitle: string;
    startDate: string;
  };
  isRequesting: boolean;
}

export interface ForeignerStatusResponse {
  foreignerProfileId: string;
  isCompletedRecommendation: boolean;
}

// 행정사 홈 최신 외국인 카드
export interface ForeignerCardResponse {
  foreignerId: string;
  nickname: string;
  nationIdList: number[];
  languageIdList: number[];
  jobTitle: string;
  degreeLevel: DegreeLevel;
}
export interface ForeignerCardRequest {
  jobGroupNameList?: string[];
  nationIdList?: number[];
  languageIdList?: number[];
  lastElementId?: string | null;
  size: number;
}

// 외국인 프로필 상세보기
export interface ForeignerProfileDetailResponse {
  basicInfo: {
    foreignerId: string;
    nickname: string;
    nationIdList: number[];
    lastAccessDay: string;
    hasChatRoomBetween: boolean;
    chatRoomId: number;
  };
  educationInfo: {
    degreeLevel: string;
    school: string;
    major: string;
  };
  languageList: number[];
  careerInfo: {
    totalCareerMonths: 0;
    history: {
      companyName: string;
      jobTitle: string;
      period: string;
      durationMonths: number;
    }[];
  };
  expectedCompanyInfo: {
    targetJob: string;
    companyName: string;
    startDate: string;
  };
}

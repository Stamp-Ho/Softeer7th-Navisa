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
    isIdle: boolean;
  };
}

export interface ForeignerStatusResponse {
  foreignerProfileId: string;
  isCompletedRecommendation: boolean;
}
export interface ForeignerCardResponse {
  foreignerId: string;
  nickname: string;
  nationIdList: number[];
  languageIdList: number[];
  jobTitle: string;
}

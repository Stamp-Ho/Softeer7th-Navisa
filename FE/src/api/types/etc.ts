// 홈 & 배지
export interface BadgeResponse {
  badgeId: number;
  badgeName: string;
}

export interface FeedbackResponse {
  feedbackId: number;
  feedbackContent: string;
  writerId: string;
  writerName: string;
  writerProfileImgUrl: string;
}

export interface HomeAgentBadgeResponse {
  reviewId: number;
  reviewerInitial: string;
  reviewContent: string;
  agentId: string;
  agentName: string;
  agentProfileImgUrl: string;
  badgeTop2: number[];
}

// 비자 신청서
export interface RecentVisaFormsResponse {
  applicationFormId: string;
  title: string;
  isDone: boolean;
  currentStep: number;
  foreignerProfileImgUrl: string;
  lastModifiedAt: string;
}
export interface ApplicationFormResponse {
  applicationFormId: string;
  foreignerProfileImgUrl: string;
  isDone: boolean;
  updatedAt: string;
  totalCount: number;
  filledCount: number;
  sections: Record<string, any>[];
}
export interface ApplicationFormRequest {
  totalCount: number;
  filledCount: number;
  sections: any[];
}
export interface PostApplicationFormResponse {
  visaFormId: string;
  updatedAt: string;
}

// 스토리지
export interface IssuePresignedUrlRequest {
  fileMimeType: string;
  fileUsage: string;
}

export interface IssuePresignedUrlResponse {
  url: string;
  objectKey: string;
}

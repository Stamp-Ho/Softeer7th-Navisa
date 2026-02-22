import type { apiClientType } from "../../hooks/useApiClient";
import type { BaseResponse, PageResponse } from "../types/common";
import * as T from "../types/agent";

export const agentService = {
  // 추천 행정사 호출
  getRecommendedAgents: async (apiClient: apiClientType, skipAuth: boolean) => {
    return await apiClient.get<BaseResponse<T.AgentCardResponse[]>>("/api/home/guest/agents", undefined, {
      skipAuth: skipAuth,
    });
  },
  getCard: (api: apiClientType, data: T.AgentCardRequest) => {
    const cleanParams = Object.fromEntries(
      Object.entries(data).filter(([_, v]) => v !== undefined && v !== null && !(Array.isArray(v) && v.length === 0)),
    );
    return api.get<BaseResponse<PageResponse<T.AgentCardResponse>>>(`/api/agent/cards`, cleanParams);
  },

  // 최신순으로 등록된 행정사 블로그 사례 3개 호출
  agentRecentFeedback: async (apiClient: apiClientType, skipAuth: boolean) => {
    return await apiClient.get<BaseResponse<T.AgentRecentFeedbackResponse[]>>("/api/home/feedback", undefined, {
      skipAuth: skipAuth,
    });
  },

  updateProfile: (api: apiClientType, data: T.RegisterAgentProfileRequest) =>
    api.post<BaseResponse<void>>("/api/agent/profile", data),

  // 행정사 프로필 상세보기
  getAgentProfileDetail: async (api: apiClientType, agentId: string) => {
    return await api.get<BaseResponse<T.AgentProfileDetailResponse>>(
      `/api/agent/${agentId}`,
      {},
      { credentials: "include" },
    );
  },

  getBadgeReview: async (apiClient: apiClientType, badgeId: number) => {
    return await apiClient.get<BaseResponse<T.AgentBadgeReviewResponse[]>>("/api/home/badge", { badgeId });
  },

  // 행정사 배지 리뷰 작성
  postBadgeReview: async (
    apiClient: apiClientType,
    data: T.PostAgentBadgeReviewRequest,
  ) => {
    return await apiClient.post<BaseResponse<string>>(
      "/api/agent/reviews",
      data,
    );
  },

  // 행정사 피드백 리뷰 작성
  postFeedbackReview: async (
    apiClient: apiClientType,
    data: T.PostAgentFeedbackReviewRequest,
  ) => {
    return await apiClient.post<BaseResponse<string>>(
      "/api/agent/feedback",
      data,
    );
  },

  // 행정사 수임종료 및 비자 신청서 복사본 생성
  patchAgentStatusFinished: async (
    apiClient: apiClientType,
    formId: string,
    data: { isFinished: boolean },
  ) => {
    return await apiClient.patch<
      BaseResponse<T.PatchAgentStatusFinishedResponse>
    >(`/api/application-forms/${formId}/status/finished`, data);
  },
};

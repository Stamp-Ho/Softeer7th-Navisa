import type { apiClientType } from "../../hooks/useApiClient";
import type { BaseResponse, PageResponse } from "../types/common";
import * as T from "../types/agent";

export const agentService = {
  // 추천 행정사 호출
  getRecommendedAgents: async (apiClient: apiClientType, skipAuth: boolean) => {
    return await apiClient.get<BaseResponse<T.AgentCardResponse[]>>(
      "/api/home/guest/agents",
      undefined,
      { skipAuth: skipAuth },
    );
  },
  getCard: (api: apiClientType, data: T.AgentCardRequest) => {
    const cleanParams = Object.fromEntries(
      Object.entries(data).filter(
        ([_, v]) => v !== undefined && v !== null && v !== "[]",
      ),
    );
    return api.get<BaseResponse<PageResponse<T.AgentCardResponse>>>(
      `/api/agent/cards`,
      cleanParams,
    );
  },

  // 최신순으로 등록된 행정사 블로그 사례 3개 호출
  agentRecentFeedback: async (apiClient: apiClientType, skipAuth: boolean) => {
    return await apiClient.get<BaseResponse<T.AgentRecentFeedbackResponse[]>>(
      "/api/home/feedback",
      undefined,
      { skipAuth: skipAuth },
    );
  },

  updateProfile: (api: apiClientType, data: T.RegisterAgentProfileRequest) =>
    api.post<BaseResponse<void>>("/api/agent/profile", data),

  // 행정사 프로필 상세보기
  getAgentProfileDetail: async (api: apiClientType, agentId: string) => {
    return await api.get<BaseResponse<T.AgentProfileDetailResponse>>(
      `/api/agent/${agentId}`,
    );
  },

  getBadgeReview: async (apiClient: apiClientType, badgeId: number) => {
    return await apiClient.get<BaseResponse<T.AgentBadgeReviewResponse[]>>(
      "/api/home/badge",
      { badgeId },
    );
  },
};

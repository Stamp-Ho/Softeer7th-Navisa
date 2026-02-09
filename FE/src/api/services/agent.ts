import type { apiClientType } from "../../hooks/useApiClient";
import type { BaseResponse, SliceResponse } from "../types/common";
import * as T from "../types/agent";

export const agentService = {
  // 추천 행정사 호출
  getRecommendedAgents: async (
    apiClient: apiClientType,
    accessToken?: string,
  ) => {
    return await apiClient.get<BaseResponse<T.AgentCardResponse[]>>(
      "/api/home/guest/agents",
      undefined,
      accessToken
        ? { headers: { Authorization: `Bearer ${accessToken}` } }
        : undefined,
    );
  },

  // 최신순으로 등록된 행정사 블로그 사례 3개 호출
  agentRecentFeedback: async (
    apiClient: apiClientType,
    accessToken?: string,
  ) => {
    return await apiClient.get<BaseResponse<T.AgentRecentFeedbackResponse[]>>(
      "/api/home/feedback",
      undefined,
      accessToken
        ? { headers: { Authorization: `Bearer ${accessToken}` } }
        : undefined,
    );
  },

  // 행정사 카드 필터 검색 (SliceResponse 활용)
  getAgentCards: async (
    apiClient: apiClientType,
    params: { size: number; lastElementId?: string; [key: string]: any },
  ) => {
    return await apiClient.get<
      BaseResponse<SliceResponse<T.AgentCardResponse>>
    >(
      "/api/agent/cards",
      { params }, // apiClient 내부에서 query string 처리가 필요함
    );
  },

  // 행정사 프로필 상세보기
  getAgentProfileDetail: async (api: apiClientType, agentId: string) => {
    return await api.get<BaseResponse<T.AgentProfileDetailResponse>>(
      `/api/agent/${agentId}`,
    );
  },

  getBadgeReview: async (apiClient: apiClientType, badgeId: number) => {
    return await apiClient.get<BaseResponse<T.AgentBadgeReviewResponse[]>>(
      "/api/home/badge",
      {
        params: { badgeId },
      },
    );
  },
};

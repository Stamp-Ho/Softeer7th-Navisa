import type { apiClientType } from "../../hooks/useApiClient";
import type {
  BaseResponse,
  PageResponse,
  SliceResponse,
} from "../types/common";
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
  getCard: (
    api: apiClientType,
    data: T.AgentCardRequest,
    accessToken?: string,
  ) => {
    // 배열 필드들을 [ "value" ] 형태의 문자열로 변환
    const params = {
      ...data,
      jobGroupNameList: data.jobGroupNameList
        ? JSON.stringify(data.jobGroupNameList)
        : undefined,
      regionList: data.regionList ? JSON.stringify(data.regionList) : undefined,
      languageIdList: data.languageIdList
        ? JSON.stringify(data.languageIdList)
        : undefined,
    };

    return api.get<PageResponse<T.AgentCardResponse>>(
      "/api/agent/cards",
      params,
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

  updateProfile: (
    api: apiClientType,
    data: T.RegisterAgentProfileRequest,
    accessToken: string,
  ) =>
    api.post<BaseResponse<void>>("/api/agent/profile", data, {
      headers: { Authorization: `Bearer ${accessToken}` },
    }),
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

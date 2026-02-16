import type { apiClientType } from "../../hooks/useApiClient";
import type { PageResponse, BaseResponse } from "../types/common";
import * as T from "../types/foreigner";

export const foreignerService = {
  getProfile: (api: apiClientType) =>
    api.get<BaseResponse<T.ForeignerRegisterRequest>>("/api/foreigner/profile"),

  updateProfile: (api: apiClientType, data: T.ForeignerRegisterRequest) =>
    api.post<BaseResponse<void>>("/api/foreigner/profile", data),

  getRequirements: (api: apiClientType) =>
    api.get<BaseResponse<T.ForeignerStatusResponse>>(
      "/api/foreigner/requirements",
    ),

  getHomeMatching: (api: apiClientType) =>
    api.get<BaseResponse<T.ForeignerCardResponse[]>>("/api/foreigner/home"),

  getCard: (api: apiClientType, data: T.ForeignerCardRequest) => {
    const cleanParams = Object.fromEntries(
      Object.entries(data).filter(
        ([_, v]) => v !== undefined && v !== null && v !== "[]",
      ),
    );
    return api.get<BaseResponse<PageResponse<T.ForeignerCardResponse>>>(
      "/api/foreigner/cards",
      cleanParams,
    );
  },
  getRecommendedForeigners: async (api: apiClientType) => {
    return await api.get<BaseResponse<T.ForeignerCardResponse[]>>(
      "/api/foreigner/home",
    );
  },

  // 외국인 프로필 상세보기
  getForeignerProfileDetail: async (
    api: apiClientType,
    foreignerId: string,
  ) => {
    return await api.get<BaseResponse<T.ForeignerProfileDetailResponse>>(
      `/api/foreigner/${foreignerId}`,
    );
  },
};

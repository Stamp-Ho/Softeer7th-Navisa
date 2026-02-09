import type { apiClientType } from "../../hooks/useApiClient";
import type { BaseResponse } from "../types/common";
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

  getRecommendedForeigners: async (api: apiClientType, accessToken: string) => {
    return await api.get<BaseResponse<T.ForeignerCardResponse[]>>(
      "/api/foreigner/home",
      undefined,
      { headers: { Authorization: `Bearer ${accessToken}` } },
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

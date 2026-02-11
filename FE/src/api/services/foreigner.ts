import type { apiClientType } from "../../hooks/useApiClient";
import type { PageResponse, BaseResponse } from "../types/common";
import * as T from "../types/foreigner";

export const foreignerService = {
  getProfile: (api: apiClientType) =>
    api.get<BaseResponse<T.ForeignerRegisterRequest>>("/api/foreigner/profile"),

  updateProfile: (
    api: apiClientType,
    data: T.ForeignerRegisterRequest,
    accessToken: string,
  ) =>
    api.post<BaseResponse<void>>("/api/foreigner/profile", data, {
      headers: { Authorization: `Bearer ${accessToken}` },
    }),

  getRequirements: (api: apiClientType) =>
    api.get<BaseResponse<T.ForeignerStatusResponse>>(
      "/api/foreigner/requirements",
    ),

  getHomeMatching: (api: apiClientType) =>
    api.get<BaseResponse<T.ForeignerCardResponse[]>>("/api/foreigner/home"),

  getCard: (
    api: apiClientType,
    data: T.ForeignerCardRequest,
    accessToken: string,
  ) => {
    const params = {
      ...data,
      jobGroupNameList: data.jobGroupNameList
        ? JSON.stringify(data.jobGroupNameList)
        : undefined,
      nationIdList: data.nationIdList
        ? JSON.stringify(data.nationIdList)
        : undefined,
      languageIdList: data.languageIdList
        ? JSON.stringify(data.languageIdList)
        : undefined,
    };
    return api.get<PageResponse<T.ForeignerCardResponse>>(
      "api/foreigner/cards",
      params,
      accessToken
        ? { headers: { Authorization: `Bearer ${accessToken}` } }
        : undefined,
    );
  },
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

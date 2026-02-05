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

  getHomeMatching: (api: apiClientType) =>
    api.get<BaseResponse<T.ForeignerCardResponse[]>>("/api/foreigner/home"),
};

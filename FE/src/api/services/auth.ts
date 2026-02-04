import type { apiClientType } from "../useApiClient";
import type { BaseResponse } from "../types/common";
import * as T from "../types/auth";

export const authService = {
  signup: (api: apiClientType, data: T.SignupRequest) =>
    api.post<BaseResponse<T.SignupResponse>>("/api/auth/signup", data),

  login: (api: apiClientType, data: T.LoginRequest) =>
    api.post<BaseResponse<T.LoginResponse>>("/api/auth/login", data),

  googleLogin: (api: apiClientType, data: T.GoogleLoginRequest) =>
    api.post<BaseResponse<T.LoginResponse>>("/api/auth/oauth/google", data),

  reissue: (api: apiClientType) =>
    api.post<BaseResponse<T.TokenResponse>>("/api/auth/reissue"),

  logout: (api: apiClientType) =>
    api.post<BaseResponse<string>>("/api/auth/logout"),
};

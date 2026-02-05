import type { BaseResponse } from "../types/common";
import type { apiClientType } from "../../hooks/useApiClient";

// homeService.ts
export const homeService = {
  getBadgeList: (api: apiClientType) =>
    api.get<BaseResponse<{ badgeId: number; badgeName: string }[]>>(
      "/api/home/badge-list",
    ),
  getAgentsByBadge: (api: apiClientType) =>
    api.get<BaseResponse<any[]>>("/api/home/badge"),
  getGuestAgents: (api: apiClientType) =>
    api.get<BaseResponse<any[]>>("/api/home/guest/agents"),
  getFeedbacks: (api: apiClientType) =>
    api.get<BaseResponse<any[]>>("/api/home/feedback"),
};

// infoService.ts
export const infoService = {
  getLanguages: (api: apiClientType) =>
    api.get<
      BaseResponse<{ languageList: { languageId: number; value: string }[] }>
    >("/api/info/languages"),
};

// visaService.ts
export const visaService = {
  getRecentForms: (api: apiClientType) =>
    api.get<BaseResponse<any[]>>("/api/visa-forms/recent-applications"),
};

// storageService.ts
export const storageService = {
  getPresignedUrl: (
    api: apiClientType,
    data: { fileMimeType: string; fileUsage: string },
  ) =>
    api.post<BaseResponse<{ url: string; objectKey: string }>>(
      "/api/storage/presigned-url",
      data,
    ),
};

import type { BaseResponse } from "../types/common";
import type { apiClientType } from "../../hooks/useApiClient";
import type { ApplicationFormRequest, ApplicationFormResponse, PatchFormStatusRequest, PatchFormStatusResponse, PostApplicationFormResponse, RecentVisaFormsResponse } from "../types/etc";

// homeService.ts
export const homeService = {
  getBadgeList: (api: apiClientType) => api.get<BaseResponse<{ badgeId: number; badgeName: string }[]>>("/api/home/badge-list"),
  getAgentsByBadge: (api: apiClientType) => api.get<BaseResponse<any[]>>("/api/home/badge"),
  getGuestAgents: (api: apiClientType) => api.get<BaseResponse<any[]>>("/api/home/guest/agents"),
  getFeedbacks: (api: apiClientType) => api.get<BaseResponse<any[]>>("/api/home/feedback"),
};

// infoService.ts
export const infoService = {
  getLanguages: (api: apiClientType) => api.get<BaseResponse<{ languageList: { languageId: number; value: string }[] }>>("/api/info/languages"),
};

// visaService.ts
export const visaService = {
  getRecentForms: (api: apiClientType) => api.get<BaseResponse<RecentVisaFormsResponse[]>>("/api/application-forms/recent-applications"),
  getApplicationForm: (api: apiClientType, formId: string) => api.get<BaseResponse<ApplicationFormResponse>>(`/api/application-forms/agent/${formId}`),
  getForeignerMyForm: (api: apiClientType) => api.get<BaseResponse<ApplicationFormResponse>>(`/api/application-forms/foreigner`),
  postApplicationForm: (api: apiClientType, formId: string, data: ApplicationFormRequest) => api.post<BaseResponse<PostApplicationFormResponse>>(`/api/application-forms/${formId}`, data),
  postApplicationFormImage: (api: apiClientType, formId: string, key: string) =>
    api.post<BaseResponse<Record<string, string>>>(`/api/application-forms/${formId}/image`, {
      profileObjectKey: key,
    }),
  patchFormStatus: (api: apiClientType, formId: string, data: PatchFormStatusRequest) => api.patch<BaseResponse<PatchFormStatusResponse>>(`/api/application-forms/${formId}/status`, data),
};

// storageService.ts
export const storageService = {
  getPresignedUrl: (api: apiClientType, data: { fileMimeType: string; fileUsage: string }) => api.post<BaseResponse<{ url: string; objectKey: string }>>("/api/storage/presigned-url", data),
};

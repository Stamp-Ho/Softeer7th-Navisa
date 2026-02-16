import { useQuery } from "@tanstack/react-query";
import { useAuth } from "../../contexts/AuthContextProvider";
import useApiClient from "../../hooks/useApiClient";
import type { RecentVisaFormsResponse } from "../types/etc";
import { visaService } from "../services/etc";

export const useRecentVisaFormsQuery = () => {
  const { apiClient } = useApiClient();
  const { userId, accessToken } = useAuth();

  return useQuery<RecentVisaFormsResponse[]>({
    queryKey: ["recentVisaForms", userId],
    queryFn: async () => {
      if (!accessToken) throw new Error("No access token");
      const res = await visaService.getRecentForms(apiClient);
      return res.result;
    },
    enabled: !!accessToken,
    retry: false, // 에러 시 재시도 금지
  });
};

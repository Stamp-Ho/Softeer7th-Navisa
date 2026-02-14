import { useQuery } from "@tanstack/react-query";
import useApiClient from "../../hooks/useApiClient";
import { useAuth } from "../../contexts/AuthContextProvider";
import type { RecentVisaFormsResponse } from "../types/etc";
import { visaService } from "../services/etc";

export const useRecentApplicationsQuery = () => {
  const { apiClient } = useApiClient();
  const { userId } = useAuth();

  return useQuery<RecentVisaFormsResponse[]>({
    queryKey: ["agentRecentForms", userId],
    queryFn: async () => {
      const res = await visaService.getRecentForms(apiClient);
      return res.result;
    },
  });
};

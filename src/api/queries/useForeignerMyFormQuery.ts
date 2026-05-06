import { useQuery } from "@tanstack/react-query";
import type { ApplicationFormResponse } from "../types/etc";
import { visaService } from "../services/etc";
import useApiClient from "../../hooks/useApiClient";
import { useAuth } from "../../contexts/AuthContextProvider";

export const useForeignerMyFormQuery = (enabled: boolean) => {
  const { apiClient } = useApiClient();
  const { userId } = useAuth();

  return useQuery<ApplicationFormResponse>({
    queryKey: ["applicationForm", userId],
    queryFn: async () => {
      const res = await visaService.getForeignerMyForm(apiClient);
      return res.result;
    },
    enabled: enabled,
    retry: false, // 에러 시 재시도 금지
  });
};

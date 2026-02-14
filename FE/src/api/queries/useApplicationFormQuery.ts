import { useQuery } from "@tanstack/react-query";
import type { ApplicationFormResponse } from "../types/etc";
import { visaService } from "../services/etc";
import useApiClient from "../../hooks/useApiClient";

export const useApplicationFormQuery = (formId: string, enabled: boolean) => {
  const { apiClient } = useApiClient();

  return useQuery<ApplicationFormResponse>({
    queryKey: ["applicationForm", formId],
    queryFn: async () => {
      const res = await visaService.getApplicationForm(apiClient, formId);
      return res.result;
    },
    enabled: enabled,
  });
};

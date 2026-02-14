import { useMutation } from "@tanstack/react-query";
import useApiClient from "../../hooks/useApiClient";
import { visaService } from "../services/etc";
import type { ApplicationFormRequest } from "../types/etc";

export const useApplicationFormMutation = (
  formId: string,
  onSuccess?: () => void,
) => {
  const { apiClient } = useApiClient();

  return useMutation({
    mutationFn: (data: ApplicationFormRequest) =>
      visaService.postApplicationForm(apiClient, formId, data),
    onSuccess: () => {
      if (onSuccess) onSuccess();
    },
    onError: (error) => {
      console.error("신청서 저장 실패: ", error);
      alert("신청서 저장에 실패했습니다.");
    },
  });
};

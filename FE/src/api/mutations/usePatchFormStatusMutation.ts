import useApiClient from "../../hooks/useApiClient";
import { visaService } from "../services/etc";
import { useMutation } from "@tanstack/react-query";

export const usePatchFormStatusMutation = (onLoginSuccess?: () => void) => {
  const { apiClient } = useApiClient();

  return useMutation({
    mutationFn: ({ formId, isDone }: { formId: string; isDone: boolean }) => visaService.patchFormStatus(apiClient, formId, { isDone: isDone }),
    onSuccess: (res) => {
      if (onLoginSuccess) onLoginSuccess();
    },
    onError: (error) => {
      console.error("신청서 상태 변경 실패: ", error);
      throw new Error("신청서 상태 변경 실패", error);
    },
  });
};

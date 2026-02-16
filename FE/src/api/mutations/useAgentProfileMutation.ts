import { useMutation } from "@tanstack/react-query";
import useApiClient from "../../hooks/useApiClient";
import { agentService } from "../services/agent";
import type { RegisterAgentProfileRequest } from "../types/agent";

export const useAgentProfileMutation = (onSuccess?: () => void) => {
  const { apiClient } = useApiClient();

  return useMutation({
    mutationFn: (data: RegisterAgentProfileRequest) =>
      agentService.updateProfile(apiClient, data),
    onSuccess: () => {
      if (onSuccess) onSuccess();
    },
    onError: (error) => {
      console.error("요건 등록 실패: ", error);
      alert("내 요건 등록에 실패했습니다.");
    },
  });
};

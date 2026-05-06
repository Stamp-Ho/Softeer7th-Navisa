import { useMutation } from "@tanstack/react-query";
import useApiClient from "../../hooks/useApiClient";
import { agentService } from "../services/agent";
import type { UpdateAgentProfileRequest } from "../types/agent";

export const useAgentProfileUpdateMutation = (onSuccess?: () => void) => {
  const { apiClient } = useApiClient();

  return useMutation({
    mutationFn: (data: UpdateAgentProfileRequest) => agentService.updateProfile(apiClient, data),
    onSuccess: () => {
      if (onSuccess) onSuccess();
    },
    onError: (error) => {
      console.error("요건 수정 실패: ", error);
      alert("내 요건 수정에 실패했습니다.");
    },
  });
};

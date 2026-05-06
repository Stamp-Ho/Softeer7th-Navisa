import { useQuery } from "@tanstack/react-query";
import useApiClient from "../../hooks/useApiClient";
import { agentService } from "../services/agent";
import type { AgentRecentFeedbackResponse } from "../types/agent";
import { useAuth } from "../../contexts/AuthContextProvider";

export const useRecentAgentFeedbackQuery = () => {
  const { apiClient } = useApiClient();
  const { userType, userId } = useAuth();

  return useQuery<AgentRecentFeedbackResponse[]>({
    queryKey: ["agentRecentFeedback", userType, userId],
    queryFn: async () => {
      const res = await agentService.agentRecentFeedback(
        apiClient,
        userType !== "VALID_AGENT",
      );
      return res.result;
    },
    retry: false, // 에러 시 재시도 금지
  });
};

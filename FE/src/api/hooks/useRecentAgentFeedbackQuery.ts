import { useQuery } from "@tanstack/react-query";
import useApiClient from "../../hooks/useApiClient";
import { agentService } from "../services/agent";
import type { AgentRecentFeedbackResponse } from "../types/agent";
import { useAuth } from "../../contexts/AuthContextProvider";

export const useRecentAgentFeedbackQuery = () => {
  const { apiClient } = useApiClient();
  const { userType, userId, accessToken } = useAuth();
  const token = userType === "VALID_AGENT" ? accessToken : undefined;

  return useQuery<AgentRecentFeedbackResponse[]>({
    queryKey: ["agentRecentFeedback", userType, userId],
    queryFn: async () => {
      const res = await agentService.agentRecentFeedback(apiClient, token);
      return res.result;
    },
  });
};

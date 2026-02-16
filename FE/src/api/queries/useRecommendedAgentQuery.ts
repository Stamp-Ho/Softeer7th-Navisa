import { useQuery } from "@tanstack/react-query";
import useApiClient from "../../hooks/useApiClient";
import { agentService } from "../services/agent";
import type { AgentCardResponse } from "../types/agent";
import { useAuth } from "../../contexts/AuthContextProvider";

export const useRecommendedAgentQuery = () => {
  const { apiClient } = useApiClient();
  const { userType } = useAuth();

  return useQuery<AgentCardResponse[]>({
    queryKey: ["recommendedAgents", userType],
    queryFn: async () => {
      const res = await agentService.getRecommendedAgents(
        apiClient,
        userType === "NOT_AUTHED",
      );
      return res.result;
    },
    retry: false, // 에러 시 재시도 금지
  });
};

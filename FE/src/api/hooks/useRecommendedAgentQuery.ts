import { useQuery } from "@tanstack/react-query";
import useApiClient from "../../hooks/useApiClient";
import { agentService } from "../services/agent";
import type { AgentCardResponse } from "../types/agent";
import { useAuth } from "../../contexts/AuthContextProvider";

export const useRecommendedAgentQuery = () => {
  const { apiClient } = useApiClient();
  const { userType, accessToken } = useAuth();
  const token = userType !== "NOT_AUTHED" ? accessToken : undefined;

  return useQuery<AgentCardResponse[]>({
    queryKey: ["recommendedAgents", userType],
    queryFn: async () => {
      const res = await agentService.getRecommendedAgents(apiClient, token);
      return res.result;
    },
  });
};

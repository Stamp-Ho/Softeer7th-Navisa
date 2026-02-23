import { useQuery } from "@tanstack/react-query";
import useApiClient from "../../hooks/useApiClient";
import { agentService } from "../services/agent";
import type { AgentCardResponse } from "../types/agent";
import { useAuth } from "../../contexts/AuthContextProvider";

export const useRecommendedAgentQuery = () => {
  const { apiClient } = useApiClient();
  const { userType } = useAuth();
  const isLoggedIn = userType !== "NOT_AUTHED";

  return useQuery<AgentCardResponse[]>({
    queryKey: ["recommendedAgents", isLoggedIn],
    queryFn: async () => {
      console.log(userType);
      const res = isLoggedIn
        ? await agentService.getRecommandedAgentsAfterLogin(apiClient)
        : await agentService.getRecommendedAgents(apiClient, true);
      return res.result;
    },
    enabled: !!apiClient,
    retry: false,
  });
};

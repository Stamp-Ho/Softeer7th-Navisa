import { useQuery } from "@tanstack/react-query";
import useApiClient from "../../hooks/useApiClient";
import { agentService } from "../services/agent";
import type { AgentCardResponse } from "../types/agent";
import { useAuth } from "../../contexts/AuthContextProvider";

export const useRecommendedAgentQuery = () => {
  const { apiClient } = useApiClient();
  const { userType, accessToken } = useAuth(); // ← 토큰 추가 가정
  const isAuthed = userType !== "NOT_AUTHED";
  const requestParams = isAuthed
    ? { headers: { Authorization: `Bearer ${accessToken}` } }
    : undefined;

  return useQuery<AgentCardResponse[]>({
    queryKey: ["recommendedAgents", userType],
    queryFn: async () => {
      const res = await agentService.recommendedAgents(
        apiClient,
        requestParams,
      );
      return res.result;
    },
  });
};

import { useQuery } from "@tanstack/react-query";
import useApiClient from "../../hooks/useApiClient";
import { agentService } from "../services/agent";
import type { AgentBadgeReviewResponse } from "../types/agent";

export const useAgentBadgeReviewQuery = (badgeId: number) => {
  const { apiClient } = useApiClient();

  return useQuery<AgentBadgeReviewResponse[]>({
    queryKey: ["badgeReview", badgeId],
    queryFn: async () => {
      const res = await agentService.getBadgeReview(apiClient, badgeId);
      return res.result;
    },
  });
};

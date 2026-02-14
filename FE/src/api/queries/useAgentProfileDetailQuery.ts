import { useQuery } from "@tanstack/react-query";
import type { AgentProfileDetailResponse } from "../types/agent";
import useApiClient from "../../hooks/useApiClient";
import { agentService } from "../services/agent";

export const useAgentProfileDetailQuery = (agentId: string) => {
  const { apiClient } = useApiClient();

  return useQuery<AgentProfileDetailResponse>({
    queryKey: ["agentDetail", agentId],
    queryFn: async () => {
      const res = await agentService.getAgentProfileDetail(apiClient, agentId);
      return res.result;
    },
  });
};

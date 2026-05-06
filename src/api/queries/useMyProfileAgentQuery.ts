import { useQuery } from "@tanstack/react-query";
import useApiClient from "../../hooks/useApiClient";
import type { feUserType } from "../../contexts/AuthContext";
import { useAuth } from "../../contexts/AuthContextProvider";
import { agentService } from "../services/agent";
import type { AgentProfileDetailResponse } from "../types/agent";

export const useMyProfileAgentQuery = (userType: feUserType) => {
  const { apiClient } = useApiClient();
  const { userId } = useAuth();

  return useQuery<AgentProfileDetailResponse>({
    queryKey: ["agentProfileDetail", userId],
    queryFn: async () => {
      const res = await agentService.getMyProfile(apiClient);
      return res.result;
    },
    enabled: userType === "VALID_AGENT", // 에이전트는 이 쿼리를 사용하지 않으므로, isAgent가 true일 때는 쿼리가 실행되지 않도록 설정
    retry: false, // 에러 시 재시도 금지
  });
};

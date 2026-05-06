import { useQuery } from "@tanstack/react-query";
import useApiClient from "../../hooks/useApiClient";
import type { ForeignerProgressResponse } from "../types/foreigner";
import { foreignerService } from "../services/foreigner";

export const useForeignerProgressQuery = (options?: {
  enabled?: boolean;
  type?: string;
}) => {
  const { apiClient } = useApiClient();

  return useQuery<ForeignerProgressResponse>({
    queryKey: ["foreignerProgress"],
    queryFn: async () => {
      const res = await foreignerService.getForeignerProgress(apiClient);
      return res.result;
    },
    enabled:
      (options?.enabled ?? true) && options?.type === "FEEDBACK_REQUIRED", // FEEDBACK_REQUIRED 상태일 때만 쿼리 활성화
    retry: false, // 에러 시 재시도 금지
  });
};

import { useQuery } from "@tanstack/react-query";
import useApiClient from "../../hooks/useApiClient";
import type { ForeignerProfileDetailResponse } from "../types/foreigner";
import { foreignerService } from "../services/foreigner";

export const useForeignerProfileDetailQuery = (foreignerId: string) => {
  const { apiClient } = useApiClient();

  return useQuery<ForeignerProfileDetailResponse>({
    queryKey: ["foreignerDetail", foreignerId],
    queryFn: async () => {
      const res = await foreignerService.getForeignerProfileDetail(
        apiClient,
        foreignerId,
      );
      return res.result;
    },
    retry: false, // 에러 시 재시도 금지
  });
};

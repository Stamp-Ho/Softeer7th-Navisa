import { useQuery } from "@tanstack/react-query";
import { useAuth } from "../../contexts/AuthContextProvider";
import useApiClient from "../../hooks/useApiClient";
import { foreignerService } from "../services/foreigner";
import type { ForeignerCardResponse } from "../types/foreigner";

export const useRecommendedForeignerQuery = () => {
  const { apiClient } = useApiClient();
  const { userId } = useAuth();

  return useQuery<ForeignerCardResponse[]>({
    queryKey: ["recommendedForeigners", userId],
    queryFn: async () => {
      const res = await foreignerService.getRecommendedForeigners(apiClient);
      return res.result;
    },
    retry: false, // 에러 시 재시도 금지
  });
};

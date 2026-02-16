import useApiClient from "../../hooks/useApiClient";
import { agentService } from "../services/agent";
import { useInfiniteQuery } from "@tanstack/react-query";
import { foreignerService } from "../services/foreigner";

export const useSearchInfiniteQuery = (
  targetType: string | null,
  filterParams: Record<string, any>,
  size: number = 15,
) => {
  const { apiClient } = useApiClient();
  const isAgent = targetType === "agent";

  return useInfiniteQuery({
    // 1. filterParams 객체 자체를 key에 넣어 변화를 감지합니다.
    queryKey: ["search", targetType, filterParams],
    queryFn: async ({ pageParam }) => {
      // 2. targetType에 따라 서비스 분기
      if (isAgent) {
        const response = await agentService.getCard(apiClient, {
          ...filterParams,
          size,
          lastElementId: pageParam as string | null,
        });
        return response;
      } else {
        const response = await foreignerService.getCard(apiClient, {
          ...filterParams,
          size,
          lastElementId: pageParam as string | null,
        });
        return response;
      }
    },
    initialPageParam: null as string | null,
    getNextPageParam: (lastPage) => {
      // API 응답 구조에 따라 existsNext가 false면 더 이상 호출 안 함
      if (!lastPage.result.existsNext || !lastPage.result.lastElementId)
        return undefined;
      return lastPage.result.lastElementId;
    },
  });
};

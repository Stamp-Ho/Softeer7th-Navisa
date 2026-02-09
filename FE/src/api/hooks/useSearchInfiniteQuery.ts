import useApiClient from "../../hooks/useApiClient";
import { agentService } from "../services/agent";
import { useInfiniteQuery } from "@tanstack/react-query";
import { foreignerService } from "../services/foreigner";
import { useAuth } from "../../contexts/AuthContextProvider";

export const useSearchInfiniteQuery = (
  targetType: string | undefined,
  filterParams: Record<string, any>,
  size: number = 10,
) => {
  const { apiClient } = useApiClient();
  const { accessToken } = useAuth(); // ← 토큰 추가 가정
  const isAgent = targetType === "agent";

  return useInfiniteQuery({
    // 1. filterParams 객체 자체를 key에 넣어 변화를 감지합니다.
    queryKey: ["search", targetType, filterParams],
    queryFn: async ({ pageParam }) => {
      // 2. targetType에 따라 서비스 분기
      if (isAgent) {
        const response = await agentService.getCard(
          apiClient,
          {
            ...filterParams,
            size,
            lastElementId: pageParam as string | undefined,
          },
          accessToken,
        );
        return response;
      } else {
        const response = await foreignerService.getCard(
          apiClient,
          {
            ...filterParams,
            size,
            lastElementId: pageParam as string | undefined,
          },
          accessToken,
        );
        return response;
      }
    },
    initialPageParam: undefined as string | undefined,
    getNextPageParam: (lastPage) => {
      if (!lastPage.existsNext || !lastPage.lastElementId) return undefined;
      return lastPage.lastElementId;
    },
  });
};

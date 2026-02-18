import { useInfiniteQuery } from "@tanstack/react-query";
import useApiClient from "../../hooks/useApiClient";
import { chatService } from "../services/chat";
import type { ChatHistoryResponse, ChatPageResponse } from "../types/chat";

export const useChatHistoryQuery = (chatRoomId: number) => {
  const { apiClient } = useApiClient();

  return useInfiniteQuery<ChatPageResponse<ChatHistoryResponse>>({
    queryKey: ["chatHistory", chatRoomId],
    queryFn: async ({ pageParam }) => {
      const res = await chatService.getChatHistory(
        apiClient,
        chatRoomId,
        pageParam !== undefined
          ? { lastElementId: pageParam as number }
          : undefined,
      );
      // result 구조 그대로 반환
      return res.result;
    },
    initialPageParam: undefined,
    getNextPageParam: (lastPage) => {
      if (!lastPage.existsNext || !lastPage.lastElementId) return undefined;
      return lastPage.lastElementId;
    },
    enabled: chatRoomId > 0,
    retry: false,
  });
};

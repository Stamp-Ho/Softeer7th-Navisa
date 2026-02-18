import { useInfiniteQuery } from "@tanstack/react-query";
import type {
  ChatPageResponse,
  ChatRoomFilter,
  ChatRoomResponse,
} from "../types/chat";
import useApiClient from "../../hooks/useApiClient";
import { chatService } from "../services/chat";

const PAGE_SIZE = 10;

export const useChatRoomsQuery = (filter: ChatRoomFilter = "all") => {
  const { apiClient } = useApiClient();

  return useInfiniteQuery<ChatPageResponse<ChatRoomResponse>>({
    queryKey: ["chatRooms", filter],
    queryFn: async ({ pageParam }) => {
      const res = await chatService.getChatRooms(
        apiClient,
        filter,
        pageParam !== undefined
          ? { lastElementId: pageParam as number, size: PAGE_SIZE }
          : undefined,
      );
      return res.result;
    },
    initialPageParam: undefined,
    getNextPageParam: (lastPage) => {
      if (!lastPage.existsNext) return undefined;
      return lastPage.lastElementId ?? undefined;
    },
    retry: false,
  });
};

import { useQuery } from "@tanstack/react-query";
import type { ChatRoomFilter, ChatRoomResponse } from "../types/chat";
import useApiClient from "../../hooks/useApiClient";
import { chatService } from "../services/chat";
import { useAuth } from "../../contexts/AuthContextProvider";

export const useChatRoomsQuery = (filter: ChatRoomFilter = "all") => {
  const { apiClient } = useApiClient();
  const { accessToken } = useAuth();

  return useQuery<ChatRoomResponse[]>({
    queryKey: ["chatRooms", filter],
    queryFn: async () => {
      const res = await chatService.getChatRooms(
        apiClient,
        filter,
        accessToken,
      );
      return res?.result?.content ?? [];
    },
    enabled: !!accessToken,
  });
};

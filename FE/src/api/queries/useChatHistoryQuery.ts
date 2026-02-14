import { useQuery } from "@tanstack/react-query";
import useApiClient from "../../hooks/useApiClient";
import { chatService } from "../services/chat";
import { useAuth } from "../../contexts/AuthContextProvider";
import type { ChatHistoryResponse } from "../types/chat";

export const useChatHistoryQuery = (chatRoomId: number) => {
  const { apiClient } = useApiClient();
  const { accessToken } = useAuth();

  return useQuery<ChatHistoryResponse[]>({
    queryKey: ["chatHistory", chatRoomId],
    queryFn: async () => {
      const res = await chatService.getChatHistory(
        apiClient,
        chatRoomId,
        accessToken,
      );
      return res.result.content;
    },
    enabled: !!accessToken && chatRoomId > 0,
  });
};

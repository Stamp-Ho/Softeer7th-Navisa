import { useQuery } from "@tanstack/react-query";
import type { ChatParticipantsInfo } from "../types/chat";
import useApiClient from "../../hooks/useApiClient";
import { chatService } from "../services/chat";

export const useChatParticipantsInfoQuery = (roomId: number) => {
  const { apiClient } = useApiClient();

  return useQuery<ChatParticipantsInfo>({
    queryKey: ["participants", roomId],
    queryFn: async () => {
      const res = await chatService.getParticipantsInfo(apiClient, roomId);
      return res.result;
    },
    retry: false,
  });
};

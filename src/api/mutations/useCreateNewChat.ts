import { useMutation } from "@tanstack/react-query";
import useApiClient from "../../hooks/useApiClient";
import { chatService } from "../services/chat";

export const useCreateNewChatMutation = () => {
  const { apiClient } = useApiClient();

  return useMutation({
    mutationFn: (data: { opponentProfileId: string; content: string; sendAt: string }) => {
      return chatService.createNewChatRoom(apiClient, data);
    },
  });
};

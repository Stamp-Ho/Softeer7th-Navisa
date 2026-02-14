import { useMutation } from "@tanstack/react-query";
import useApiClient from "../../hooks/useApiClient";
import type { Send } from "../websocket/types";
import { chatService } from "../services/chat";
import { v4 as uuidv4 } from "uuid";
import { useAuth } from "../../contexts/AuthContextProvider";

export const usePostProposal = (roomId: number) => {
  const { apiClient } = useApiClient();
  const { userId } = useAuth();

  return useMutation({
    mutationFn: () => {
      const data: Send = {
        roomId,
        clientMessageId: uuidv4(),
        senderId: userId,
        content: "PROPOSAL",
        type: "PROPOSAL",
        sentAt: new Date().toISOString(),
      };
      return chatService.postProposal(apiClient, roomId, data);
    },
  });
};

export const usePostProposalRejected = (roomId: number) => {
  const { apiClient } = useApiClient();
  const { userId } = useAuth();

  return useMutation({
    mutationFn: () => {
      const data: Send = {
        roomId,
        clientMessageId: uuidv4(),
        senderId: userId,
        content: "REJECTED",
        type: "REJECTED",
        sentAt: new Date().toISOString(),
      };
      return chatService.postRejected(apiClient, roomId, data);
    },
  });
};

export const usePostProposalAccepted = (roomId: number) => {
  const { apiClient } = useApiClient();
  const { userId } = useAuth();

  return useMutation({
    mutationFn: () => {
      const data: Send = {
        roomId,
        clientMessageId: uuidv4(),
        senderId: userId,
        content: "ACCEPTED",
        type: "ACCEPTED",
        sentAt: new Date().toISOString(),
      };
      return chatService.postAccepted(apiClient, roomId, data);
    },
  });
};

export const usePostProposalCanceled = (roomId: number) => {
  const { apiClient } = useApiClient();
  const { userId } = useAuth();

  return useMutation({
    mutationFn: () => {
      const data: Send = {
        roomId,
        clientMessageId: uuidv4(),
        senderId: userId,
        content: "CANCELED",
        type: "CANCELED",
        sentAt: new Date().toISOString(),
      };
      return chatService.postCanceled(apiClient, roomId, data);
    },
  });
};

import { useQuery } from "@tanstack/react-query";
import useApiClient from "../../hooks/useApiClient";
import { chatService } from "../services/chat";
import { useAuth } from "../../contexts/AuthContextProvider";

export const useChatUnreadCount = () => {
  const { apiClient } = useApiClient();
  const { accessToken } = useAuth();

  return useQuery({
    queryKey: ["chatUnreadCount"],
    queryFn: async () => {
      const res = await chatService.getUnreadCount(apiClient, accessToken);
      return res.result;
    },
    enabled: !!accessToken,
  });
};

export const useChatMatchedUnreadCount = (options?: { enabled?: boolean }) => {
  const { apiClient } = useApiClient();
  const { accessToken } = useAuth();

  return useQuery({
    queryKey: ["chatMatchedUnreadCount"],
    queryFn: async () => {
      const res = await chatService.getMatchedUnreadCount(
        apiClient,
        accessToken,
      );
      return res.result;
    },
    enabled: (options?.enabled ?? true) && !!accessToken,
  });
};

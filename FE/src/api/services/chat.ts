import type { apiClientType } from "../../hooks/useApiClient";
import type {
  ChatHistoryResponse,
  ChatRoomFilter,
  ChatRoomResponse,
} from "../types/chat";
import type { BaseResponse, PageResponse } from "../types/common";

export const chatService = {
  // 채팅방 목록 조회 (필터: unread | matched)
  getChatRooms: async (
    apiClient: apiClientType,
    filter: ChatRoomFilter,
    accessToken: string,
  ) => {
    const query = filter === "all" ? "" : `?filter=${filter}`;

    return await apiClient.get<BaseResponse<PageResponse<ChatRoomResponse>>>(
      `/api/chatrooms${query}`,
      undefined,
      { headers: { Authorization: `Bearer ${accessToken}` } },
    );
  },

  getUnreadCount: async (apiClient: apiClientType, accessToken: string) => {
    return await apiClient.get<BaseResponse<{ count: number }>>(
      "/api/chatrooms/nonread/count",
      undefined,
      { headers: { Authorization: `Bearer ${accessToken}` } },
    );
  },

  getMatchedUnreadCount: async (
    apiClient: apiClientType,
    accessToken: string,
  ) => {
    return await apiClient.get<BaseResponse<{ count: number }>>(
      "/api/chatrooms/matched/count",
      undefined,
      { headers: { Authorization: `Bearer ${accessToken}` } },
    );
  },

  getChatHistory: async (
    apiClient: apiClientType,
    chatRoomId: number,
    accessToken: string,
  ) => {
    return await apiClient.get<BaseResponse<PageResponse<ChatHistoryResponse>>>(
      `/api/chatroom/${chatRoomId}/messages`,
      undefined,
      { headers: { Authorization: `Bearer ${accessToken}` } },
    );
  },
};

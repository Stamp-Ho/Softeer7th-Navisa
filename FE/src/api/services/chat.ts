import type { apiClientType } from "../../hooks/useApiClient";
import type {
  ChatHistoryResponse,
  ChatRoomFilter,
  ChatRoomResponse,
} from "../types/chat";
import type { BaseResponse, PageResponse } from "../types/common";
import type { Send } from "../websocket/types";

export const chatService = {
  createNewChatRoom: (
    api: apiClientType,
    data: {
      opponentProfileId: string;
      content: string;
      sendAt: string;
    },
  ) => api.post<BaseResponse<number>>(`/api/chatroom`, data),

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

  postProposal: (api: apiClientType, roomId: number, data: Send) =>
    api.post<BaseResponse<string>>(`/api/chatroom/${roomId}/proposal`, data),

  postRejected: (api: apiClientType, roomId: number, data: Send) =>
    api.post<BaseResponse<string>>(
      `/api/chatroom/${roomId}/proposal/rejected`,
      data,
    ),

  postAccepted: (api: apiClientType, roomId: number, data: Send) =>
    api.post<BaseResponse<string>>(
      `/api/chatroom/${roomId}/proposal/accepted`,
      data,
    ),

  postCanceled: (api: apiClientType, roomId: number, data: Send) =>
    api.post<BaseResponse<string>>(
      `/api/chatroom/${roomId}/proposal/canceled`,
      data,
    ),
};

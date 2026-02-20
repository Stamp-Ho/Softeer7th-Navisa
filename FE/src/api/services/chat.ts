import type { apiClientType } from "../../hooks/useApiClient";
import type {
  ChatHistoryResponse,
  ChatPageResponse,
  ChatParticipantsInfo,
  ChatRoomFilter,
  ChatRoomResponse,
} from "../types/chat";
import type { BaseResponse } from "../types/common";
import type { Send } from "../websocket/types";

export const chatService = {
  // 채팅방 생성
  createNewChatRoom: (
    api: apiClientType,
    data: {
      opponentProfileId: string;
      content: string;
      sendAt: string;
    },
  ) => api.post<BaseResponse<number>>(`/api/chatroom`, data),

  // 특정 채팅방 참여자 정보 조회
  getParticipantsInfo: async (apiClient: apiClientType, roomId: number) => {
    return await apiClient.get<BaseResponse<ChatParticipantsInfo>>(
      `/api/chatrooms/${roomId}/participants-info`,
    );
  },

  // 채팅방 목록 조회 (필터: unread | matched)
  getChatRooms: async (
    apiClient: apiClientType,
    filter?: ChatRoomFilter,
    params?: { lastElementId?: number; size?: number },
  ) => {
    const requestParams = Object.fromEntries(
      Object.entries({
        ...params,
        filter: filter === "all" ? undefined : filter,
      }).filter(([, v]) => v !== undefined),
    );

    return await apiClient.get<
      BaseResponse<ChatPageResponse<ChatRoomResponse>>
    >(`/api/chatrooms`, requestParams);
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
    params?: { lastElementId?: number },
  ) => {
    return await apiClient.get<
      BaseResponse<ChatPageResponse<ChatHistoryResponse>>
    >(`/api/chatroom/${chatRoomId}/messages`, params);
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

  postBlocked: (api: apiClientType, roomId: number, data: Send) =>
    api.post<BaseResponse<string>>(`/api/chatroom/${roomId}/block`, data),
};

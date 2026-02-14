import type { ChatRoomStatus } from "../hooks/useChatRoom";

export const deriveStatusFromMessageType = (
  type: string,
  currentStatus: ChatRoomStatus,
): ChatRoomStatus => {
  switch (type) {
    case "PROPOSAL":
      return "PROPOSED";
    case "ACCEPTED":
      return "MATCHED";
    case "REJECTED":
    case "CANCELED":
      return "DEFAULT";
    default:
      return currentStatus; // 시스템 메시지가 아니면 기존 상태 유지
  }
};

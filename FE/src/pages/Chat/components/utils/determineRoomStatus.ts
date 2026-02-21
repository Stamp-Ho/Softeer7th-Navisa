import type { ChatRoomStatus } from "../hooks/useChatRoom";

/**
 * 채팅방 상태 결정 로직
 * 우선순위: CHATROOM_BLOCKED > proposalMatched > proposed > DEFAULT
 *
 * @param roomStatus - 서버에서 받은 roomStatus ("DEFAULT" 또는 "BLOCKED")
 * @param proposed - 수임제안 여부
 * @param proposalMatched - 매칭 여부
 * @returns 결정된 채팅방 상태
 */
export const determineRoomStatus = (
  roomStatus: string | undefined,
  proposed: boolean,
  proposalMatched: boolean,
): ChatRoomStatus => {
  // 1순위: BLOCKED
  if (roomStatus === "BLOCKED" || roomStatus === "CHATROOM_BLOCKED") {
    return "CHATROOM_BLOCKED";
  }

  // 2순위: proposalMatched
  if (proposalMatched) {
    return "MATCHED";
  }

  // 3순위: proposed
  if (proposed) {
    return "PROPOSED";
  }

  // 4순위: DEFAULT
  return "DEFAULT";
};

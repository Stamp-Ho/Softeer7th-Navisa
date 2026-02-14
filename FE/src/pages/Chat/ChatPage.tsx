import { useMemo, useState } from "react";
import ChatRoomList from "./components/ChatList/ChatRoomList";
import Envelope from "../../assets/Envelope";
import ChatRoom from "./components/ChatRoom/ChatRoom";
import ChatRoomModal from "./components/Modal/ChatRoomModal";
import ReviewModal from "./components/Review/ReviewModal";
import NoChatView from "./components/ChatRoom/NoChatView";
import type { ChatRoomFilter } from "../../api/types/chat";
import ChatRoomTabButton from "./components/ChatRoom/ChatRoomTabButton";
import {
  useChatMatchedUnreadCount,
  useChatUnreadCount,
} from "../../api/queries/useChatUnreadCountQuery";
import { useChatRoomsQuery } from "../../api/queries/useChatRoomsQuery";
import { useAuth } from "../../contexts/AuthContextProvider";
import { useWebSocket } from "../../contexts/WebSocketContext";
import { deriveStatusFromMessageType } from "./components/utils/getChatStatus";
import type { ChatRoomStatus } from "./components/hooks/useChatRoom";

const ChatPage = () => {
  const [selectedChatRoomId, setSelectedChatRoomId] = useState<number>(-1);
  const [selectedTab, setSelectedTab] = useState<ChatRoomFilter>("all");
  const [viewMessageModal, setViewMessageModal] = useState<number>(0);
  const [reviewModal, setReviewModal] = useState<number>(0);
  const [opponentImg, setOpponentImg] = useState<string | null>(null);

  const { messages: socketMessages } = useWebSocket(); // 전역 웹소켓 메시지 구독

  // ======== Auth ========
  const { userType } = useAuth();
  const isAgent = userType === "VALID_AGENT";
  const isFileReady =
    userType === "VALID_AGENT" || userType === "FILLED_FOREIGNER";

  // ======== API ========
  const {
    data: unreadCountData,
    isLoading: isUnreadLoading,
    isError: isUnreadError,
  } = useChatUnreadCount();
  const unreadCount = unreadCountData?.count ?? 0;

  const {
    data: matchedUnreadCountData,
    isLoading: isMatchedUnreadLoading,
    isError: isMatchedUnreadError,
  } = useChatMatchedUnreadCount({ enabled: isAgent });
  const matchedUnreadCount = matchedUnreadCountData?.count ?? 0;

  const {
    data: chatRooms = [],
    isLoading: isChatRoomsLoading,
    isError: isChatRoomsError,
  } = useChatRoomsQuery(selectedTab);

  const syncedChatRooms = useMemo(() => {
    return chatRooms.map((room) => {
      // 해당 방에 대한 새로운 소켓 메시지들 필터링
      const roomSocketMsgs = socketMessages.filter(
        (m) => Number(m.roomId) === room.chatRoomId,
      );

      if (roomSocketMsgs.length === 0) return room;

      // 가장 최신 소켓 메시지를 기준으로 방 정보 업데이트
      const latestMsg = roomSocketMsgs[roomSocketMsgs.length - 1];

      // 수임 관련 메시지만 필터링
      const PROPOSAL_TYPES = new Set([
        "ACCEPTED",
        "CANCELED",
        "PROPOSAL",
        "REJECTED",
      ]);
      const proposalMsgs = roomSocketMsgs.filter((m) =>
        PROPOSAL_TYPES.has(m.type),
      );

      return {
        ...room,
        // 마지막 메시지 갱신
        lastMessage: latestMsg.content,
        lastChattedAt: latestMsg.createdAt,
        // 실시간 수임 상태 반영
        roomStatus:
          proposalMsgs.length > 0
            ? deriveStatusFromMessageType(
                proposalMsgs[proposalMsgs.length - 1].type,
                room.roomStatus as ChatRoomStatus,
              )
            : room.roomStatus,
      };
    });
  }, [chatRooms, socketMessages]);

  const isChatExist = chatRooms.length > 0;

  const onModalAction = (num: number) => setViewMessageModal(num);
  const onSelectChat = (id: number, profileImg: string | null) => {
    setSelectedChatRoomId(id);
    setOpponentImg(profileImg);
  };
  const onCloseChat = () => {
    setSelectedChatRoomId(-1);
    setOpponentImg(null);
  };
  const reviewHandler = (num: number) => setReviewModal(num);

  if (!isFileReady || (!isChatExist && selectedTab === "all"))
    return <NoChatView isFileReady={isFileReady} />;

  return (
    <>
      <div className="fixed inset-0 bg-background-sub -z-10"></div>

      {reviewModal > 0 && (
        <ReviewModal reviewHandler={reviewHandler} modalView={reviewModal} />
      )}

      {viewMessageModal > 0 && (
        <ChatRoomModal
          onModalAction={onModalAction}
          modalView={viewMessageModal}
          roomId={selectedChatRoomId}
        />
      )}

      {/* ================= 메인 레이아웃 ================= */}
      <div className="relative flex flex-row justify-between mt-12 h-[904px]">
        {/* ===== 왼쪽: 탭 + 목록 ===== */}
        <div className="flex flex-col">
          <div className="headline-m-bold text-gray-1000 mb-13">
            상담 메시지
          </div>

          <div className="flex flex-row gap-3 px-3 mb-10">
            <ChatRoomTabButton
              label="전체"
              value="all"
              selectedTab={selectedTab}
              width="w-[92px]"
              onClick={() => {
                setSelectedTab("all");
                setSelectedChatRoomId(-1);
              }}
            />

            <ChatRoomTabButton
              label="안 읽음"
              value="unread"
              selectedTab={selectedTab}
              count={isUnreadLoading || isUnreadError ? 0 : unreadCount}
              onClick={() => {
                setSelectedTab("unread");
                setSelectedChatRoomId(-1);
              }}
            />

            {isAgent && (
              <ChatRoomTabButton
                label="수임 중"
                value="matched"
                selectedTab={selectedTab}
                count={
                  isMatchedUnreadLoading || isMatchedUnreadError
                    ? 0
                    : matchedUnreadCount
                }
                onClick={() => {
                  setSelectedTab("matched");
                  setSelectedChatRoomId(-1);
                }}
              />
            )}
          </div>

          {/* ===== 전체 페이지 로딩 ===== */}
          {isChatRoomsLoading && (
            <div className="px-3 text-gray-500">로딩 중...</div>
          )}

          {/* ===== 목록 (에러면 chatRooms 안 넘김 = 더미 사용) ===== */}
          {!isChatRoomsLoading && (
            <ChatRoomList
              chatRooms={isChatRoomsError ? [] : syncedChatRooms}
              onSelectChat={onSelectChat}
              selectedChatRoomId={selectedChatRoomId}
            />
          )}
        </div>
        {/* ===== 오른쪽: 채팅창 ===== */}
        {selectedTab === "unread" &&
        unreadCount === 0 &&
        !isChatRoomsError &&
        !isUnreadLoading ? (
          <div className="absolute flex justify-center top-[40%] w-full headline-s-medium text-gray-500 ">
            읽지 않은 메시지가 없어요.
          </div>
        ) : (
          <div className="relative flex flex-col w-[880px] h-full rounded-[20px] overflow-hidden bg-gray-0">
            {selectedChatRoomId === -1 && (
              <div className="flex flex-col items-center my-auto">
                <Envelope />
                <div className="mt-7 headline-s-medium text-gray-500">
                  대화할 메시지를 선택해 주세요.
                </div>
              </div>
            )}

            {selectedChatRoomId !== -1 && (
              <div className="flex flex-col justify-between h-full">
                <div className="w-full pt-10"></div>
                <ChatRoom
                  chatRoomId={selectedChatRoomId}
                  onClose={onCloseChat}
                  onModalAction={onModalAction}
                  profileImg={opponentImg}
                />
                <div className="w-full pt-28"></div>
              </div>
            )}
          </div>
        )}
      </div>

      <button onClick={() => setReviewModal(1)}>리뷰1</button>
      <button onClick={() => setReviewModal(2)}>리뷰2</button>
    </>
  );
};

export default ChatPage;

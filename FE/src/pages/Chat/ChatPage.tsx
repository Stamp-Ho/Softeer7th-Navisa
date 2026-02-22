import { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useLocation, useSearchParams } from "react-router-dom";
import ChatRoomList from "./components/ChatList/ChatRoomList";
import Envelope from "../../assets/Envelope";
import ChatRoom from "./components/ChatRoom/ChatRoom";
import ChatRoomModal from "./components/Modal/ChatRoomModal";
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
import { useSyncedChatRooms } from "./components/hooks/useSyncedChatRooms";

const ChatPage = () => {
  const [searchParams] = useSearchParams();
  const chatRoomNumber = Number(searchParams.get("chatroom"));

  const { t } = useTranslation(["pages"]);
  const location = useLocation();
  const [selectedChatRoomId, setSelectedChatRoomId] = useState<number>(
    chatRoomNumber || -1,
  );
  const [selectedTab, setSelectedTab] = useState<ChatRoomFilter>("all");
  const [viewMessageModal, setViewMessageModal] = useState<number>(0);
  const [opponentImg, setOpponentImg] = useState<string | null>(null);

  const { messages: socketMessages } = useWebSocket(); // 전역 웹소켓 메시지 구독
  const { userId, userType } = useAuth();

  // ======== Auth ========
  const isAgent = userType === "VALID_AGENT";
  const isFileReady =
    userType === "VALID_AGENT" || userType === "FILLED_FOREIGNER";

  // ======== API ========
  const { data: unreadCountData } = useChatUnreadCount();
  const unreadCount = unreadCountData?.count ?? 0;

  const { data: matchedUnreadCountData } = useChatMatchedUnreadCount({
    enabled: isAgent,
  });
  const matchedUnreadCount = matchedUnreadCountData?.count ?? 0;

  const {
    data,
    isLoading: isChatRoomsLoading,
    isError: isChatRoomsError,
    fetchNextPage,
    isFetchingNextPage,
    hasNextPage,
  } = useChatRoomsQuery(selectedTab);
  const chatRooms = data?.pages.flatMap((page) => page.content) ?? [];

  // 채팅방 목록 실시간 연동 (수임상태, 안읽음 개수)
  const { syncedChatRooms } = useSyncedChatRooms({
    chatRooms,
    socketMessages,
    selectedChatRoomId,
    userId,
    selectedTab,
  });

  const isChatExist = chatRooms.length > 0;

  // FloatingChatModal에서 location.state로 전달된 chatRoomId 처리
  useEffect(() => {
    if (location.state?.selectedChatRoomId) {
      setSelectedChatRoomId(location.state.selectedChatRoomId);
      // state 사용 후 초기화 (선택사항, URL에 state가 남지 않게)
      window.history.replaceState({}, document.title, window.location.pathname);
    }
  }, [location.state?.selectedChatRoomId]);

  const onModalAction = (num: number) => setViewMessageModal(num);
  const onSelectChat = (id: number, profileImg: string | null) => {
    setSelectedChatRoomId(id);
    setOpponentImg(profileImg);
  };
  const onCloseChat = () => {
    setSelectedChatRoomId(-1);
    setOpponentImg(null);
  };

  if (
    !isFileReady ||
    (!isChatRoomsLoading && !isChatExist && selectedTab === "all")
  )
    return <NoChatView isFileReady={isFileReady} />;

  return (
    <>
      <div className="fixed inset-0 bg-background-sub -z-10"></div>
      {viewMessageModal > 0 && (
        <ChatRoomModal
          onModalAction={onModalAction}
          modalView={viewMessageModal}
          roomId={selectedChatRoomId}
        />
      )}

      {/* ================= 메인 레이아웃 ================= */}
      <div className="relative flex flex-row justify-between mt-12 h-[824px]">
        {/* ===== 왼쪽: 탭 + 목록 ===== */}
        <div className="flex flex-col">
          <div className="headline-m-bold text-gray-1000 mb-13">
            {t("chat.title")}
          </div>

          <div className="flex flex-row gap-3 px-3 mb-10">
            <ChatRoomTabButton
              label={t("chat.tabs.all")}
              value="all"
              selectedTab={selectedTab}
              width="w-[92px]"
              onClick={() => {
                setSelectedTab("all");
                setSelectedChatRoomId(-1);
              }}
            />

            <ChatRoomTabButton
              label={t("chat.tabs.unread")}
              value="unread"
              selectedTab={selectedTab}
              count={unreadCount}
              onClick={() => {
                setSelectedTab("unread");
                setSelectedChatRoomId(-1);
              }}
            />

            {isAgent && (
              <ChatRoomTabButton
                label={t("chat.tabs.inContract")}
                value="matched"
                selectedTab={selectedTab}
                count={matchedUnreadCount}
                onClick={() => {
                  setSelectedTab("matched");
                  setSelectedChatRoomId(-1);
                }}
              />
            )}
          </div>

          {/* ===== 전체 페이지 로딩 ===== */}
          {isChatRoomsLoading && (
            <div className="px-3 text-gray-500">{t("search.loading")}</div>
          )}

          {/* ===== 목록 (에러면 chatRooms 안 넘김 = 더미 사용) ===== */}
          {!isChatRoomsLoading && (
            <ChatRoomList
              chatRooms={isChatRoomsError ? [] : syncedChatRooms}
              onSelectChat={onSelectChat}
              selectedChatRoomId={selectedChatRoomId}
              fetchNextPage={fetchNextPage}
              isFetchingNextPage={isFetchingNextPage}
              hasNextPage={hasNextPage ?? false}
            />
          )}
        </div>
        {/* ===== 오른쪽: 채팅창 ===== */}
        {selectedTab === "unread" &&
        unreadCount === 0 &&
        selectedChatRoomId === -1 &&
        !isChatRoomsError &&
        !isChatRoomsLoading ? (
          <div className="absolute flex justify-center top-[40%] w-full headline-s-medium text-gray-500 ">
            {t("chat.noUnreadMessages")}
          </div>
        ) : (
          <div className="relative flex flex-col w-[880px] h-full rounded-[20px] overflow-hidden bg-gray-0">
            {selectedChatRoomId === -1 && (
              <div className="flex flex-col items-center my-auto">
                <Envelope />
                <div className="mt-7 headline-s-medium text-gray-500">
                  {t("chat.selectMessage")}
                </div>
              </div>
            )}

            {selectedChatRoomId !== -1 && (
              <div className="flex flex-col justify-between h-full">
                <ChatRoom
                  chatRoomId={selectedChatRoomId}
                  onClose={onCloseChat}
                  onModalAction={onModalAction}
                  profileImg={opponentImg}
                  initialRoomStatus={
                    syncedChatRooms.find(
                      (r) => r.chatRoomId === selectedChatRoomId,
                    )?.roomStatus
                  }
                />
              </div>
            )}
          </div>
        )}
      </div>
    </>
  );
};

export default ChatPage;

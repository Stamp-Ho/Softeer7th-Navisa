import { useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import ChatRoomCard from "./ChatRoomCard";
import type { ChatRoomResponse } from "../../../../api/types/chat";

type ChatRoomListProps = {
  chatRooms: ChatRoomResponse[];
  onSelectChat: (id: number, profileImg: string | null) => void;
  selectedChatRoomId: number | null;
  fetchNextPage: () => void;
  isFetchingNextPage: boolean;
  hasNextPage: boolean;
};

const ChatRoomList = ({
  chatRooms,
  onSelectChat,
  selectedChatRoomId,
  fetchNextPage,
  isFetchingNextPage,
  hasNextPage,
}: ChatRoomListProps) => {
  const { t } = useTranslation(["components"]);
  const scrollRef = useRef<HTMLDivElement>(null);
  const endRef = useRef<HTMLDivElement>(null);

  const [isAtStart, setIsAtStart] = useState(true);
  const [isAtEnd, setIsAtEnd] = useState(false);

  const handleScroll = () => {
    if (scrollRef.current) {
      const { scrollTop, scrollHeight, clientHeight } = scrollRef.current;

      // scrollHeight(전체높이) - scrollTop(내려온길이) - clientHeight(보이는높이) = 화면 아래의 높이
      const bottomHeight = scrollHeight - scrollTop - clientHeight;

      setIsAtStart(scrollTop <= 20); // 상단 도달 체크 (여유값 20px)
      setIsAtEnd(bottomHeight <= 20); //스타일 적용

      if (bottomHeight <= 50 && !isFetchingNextPage && hasNextPage) {
        fetchNextPage();
      } //다음 페이지 가져오기
    }
  };

  const searchResultStyle = () => {
    const base = "transition-all duration-500 ";
    if (isAtStart)
      return (
        base +
        `mask-[linear-gradient(to_bottom,black_90%,transparent_100%)]
        [-webkit-mask-image:linear-gradient(to_bottom,black_90%,transparent_100%)]`
      );
    if (isAtEnd)
      return (
        base +
        `mask-[linear-gradient(to_top,black_90%,transparent_100%)]
        [-webkit-mask-image:linear-gradient(to_top,black_90%,transparent_100%)]`
      );
    return (
      base +
      `mask-[linear-gradient(to_bottom,transparent_0%,black_10%,black_90%,transparent_100%)]
      [-webkit-mask-image:linear-gradient(to_bottom,transparent_0%,black_10%,black_90%,transparent_100%)]`
    );
  };

  const sortedChatRoom = sortChatRoom(chatRooms);

  return (
    <div
      ref={scrollRef}
      onScroll={handleScroll}
      className={`w-[602px] h-full overflow-auto pr-6 -mr-6 ${searchResultStyle()}`}
    >
      {sortedChatRoom.map((data) => (
        <div
          key={data.chatRoomId}
          className={
            selectedChatRoomId === data.chatRoomId
              ? "bg-white rounded-[10px]"
              : ""
          }
          onClick={() => onSelectChat(data.chatRoomId, data.profileImgUrl)}
        >
          <ChatRoomCard
            profileImgUrl={data.profileImgUrl ?? ""}
            opponentName={data.opponentName}
            roomStatus={data.roomStatus}
            lastMessage={data.lastMessage}
            noneRead={data.noneReadCount}
            lastChattedAt={data.lastChattedAt}
          />
        </div>
      ))}

      {isFetchingNextPage && (
        <div className="py-4 text-center text-gray-400">
          {t("chatRoom.loading")}
        </div>
      )}

      <div ref={endRef} className="h-4" />
    </div>
  );
};

export default ChatRoomList;

const sortChatRoom = (arr: ChatRoomResponse[]) => {
  const matched = arr.filter((r) => r.roomStatus === "MATCHED");
  const unmatched = arr.filter((r) => r.roomStatus !== "MATCHED");

  return [...matched, ...unmatched];
};

import { useEffect, useRef, useState } from "react";
import ChatRoomCard from "./ChatRoomCard";

type ChatRoomListProps = {
  chatList: ChatRoomCardParams[];
  onSelectChat: (id: number) => void;
  selectedTab: number;
  selectedChatRoomId: number | null;
};

type ChatRoomCardParams = {
  chatRoomId: number;
  profileImgUrl: string;
  opponentName: string;
  roomStatus: string;
  lastMessage: string;
  noneRead: number;
  lastChattedAt: string;
};

const ChatRoomList = ({
  chatList,
  onSelectChat,
  // selectedTab,
  selectedChatRoomId,
}: ChatRoomListProps) => {
  const scrollRef = useRef<HTMLDivElement>(null);
  const endRef = useRef<HTMLDivElement>(null);

  const [tempNumber, setTempNumber] = useState(1);

  useEffect(() => {
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          setTempNumber((prev) => prev + 1);
        }
      },
      { threshold: 0.1 },
    );

    if (endRef.current) observer.observe(endRef.current);
    return () => observer.disconnect();
  }, []);

  const getMaskStyle = `transition-all duration-500 mask-[linear-gradient(to_bottom,transparent_0%,black_5%,black_90%,transparent_100%)]
                        [-webkit-mask-image:linear-gradient(to_bottom,transparent_0%,black_5%,black_90%,transparent_100%)]`;

  const sortedChatRoom = sortChatRoom(chatList);

  return (
    <div
      ref={scrollRef}
      className={`w-[602px] h-full overflow-auto pr-6 -mr-6 ${getMaskStyle}`}
    >
      {sortedChatRoom.slice(0, 20 * tempNumber).map((data) => (
        <div
          className={
            selectedChatRoomId === data.chatRoomId
              ? "bg-white rounded-[10px]"
              : ""
          }
          key={data.chatRoomId}
          onClick={() => onSelectChat(data.chatRoomId)}
        >
          <ChatRoomCard
            chatRoomId={data.chatRoomId}
            profileImgUrl={data.profileImgUrl}
            opponentName={data.opponentName}
            roomStatus={data.roomStatus}
            lastMessage={data.lastMessage}
            noneRead={data.noneRead}
            lastChattedAt={data.lastChattedAt}
          />
        </div>
      ))}
      <div ref={endRef} className="w-3 h-1 -mt-10" />
    </div>
  );
};

export default ChatRoomList;

const sortChatRoom = (arr: ChatRoomCardParams[]) => {
  const matched = arr.filter((r) => r.roomStatus === "MATCHED");
  const unmatched = arr.filter((r) => r.roomStatus !== "MATCHED");

  return [...matched, ...unmatched];
};

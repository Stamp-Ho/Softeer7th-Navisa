import { useEffect, useRef, useState } from "react";
import ChatRoomCard from "./ChatRoomCard";

type ChatRoomListProps = {
  chatList: ChatRoomCardParams[];
  onSelectChat: (id: number | null) => void;
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
  selectedChatRoomId,
  // selectedTab,
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

  const matched: ChatRoomCardParams[] = [];
  const unMatched: ChatRoomCardParams[] = [];

  sortChatRoom(chatList, matched, unMatched);

  return (
    <div
      ref={scrollRef}
      className={`w-[602px] h-full overflow-auto pr-6 -mr-6 ${getMaskStyle}`}
    >
      {unMatched.slice(0, 20 * tempNumber).map((data) => (
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

const sortChatRoom = (
  arr: ChatRoomCardParams[],
  a: ChatRoomCardParams[],
  b: ChatRoomCardParams[],
) => {
  arr.map((data) => {
    if (data.roomStatus === "MATCHED") {
      a.push(data);
    } else {
      b.push(data);
    }
  });
};

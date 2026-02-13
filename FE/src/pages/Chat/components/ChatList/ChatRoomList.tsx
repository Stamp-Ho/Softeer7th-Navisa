import { useEffect, useRef, useState } from "react";
import ChatRoomCard from "./ChatRoomCard";
import type { ChatRoomResponse } from "../../../../api/types/chat";

type ChatRoomListProps = {
  chatRooms?: ChatRoomResponse[];
  onSelectChat: (
    id: number,
    matched: boolean,
    profileImg: string | null,
  ) => void;
  selectedChatRoomId: number | null;
};

const dummyData: ChatRoomResponse[] = [
  {
    chatRoomId: 0,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneReadCount: 100,
    lastChattedAt: "2026-02-10T08:00:55.896Z",
    proposed: true,
    proposalMatched: true,
  },
  {
    chatRoomId: 2,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "PROPOSED",
    lastMessage: "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다...",
    noneReadCount: 2,
    lastChattedAt: "2026-02-10T08:00:55.896Z",
    proposed: true,
    proposalMatched: true,
  },
  {
    chatRoomId: 3,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "MATCHED",
    lastMessage: "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다...",
    noneReadCount: 2,
    lastChattedAt: "2026-02-10T08:00:55.896Z",
    proposed: true,
    proposalMatched: true,
  },
];

const ChatRoomList = ({
  chatRooms = dummyData,
  onSelectChat,
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

  const sortedChatRoom = sortChatRoom(chatRooms);

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
          onClick={() =>
            onSelectChat(
              data.chatRoomId,
              data.proposalMatched,
              data.profileImgUrl,
            )
          }
        >
          <ChatRoomCard
            chatRoomId={data.chatRoomId}
            profileImgUrl={data.profileImgUrl ?? ""}
            opponentName={data.opponentName}
            roomStatus={data.roomStatus}
            lastMessage={data.lastMessage}
            noneRead={data.noneReadCount}
            lastChattedAt={data.lastChattedAt}
          />
        </div>
      ))}
      <div ref={endRef} className="w-3 h-1 -mt-10" />
    </div>
  );
};

export default ChatRoomList;

const sortChatRoom = (arr: ChatRoomResponse[]) => {
  const matched = arr.filter((r) => r.roomStatus === "MATCHED");
  const unmatched = arr.filter((r) => r.roomStatus !== "MATCHED");

  return [...matched, ...unmatched];
};

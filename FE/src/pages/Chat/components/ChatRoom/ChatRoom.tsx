import type { ChatRoomHeaderData } from "../../../../types/chatRoomTypes";
import ChatBody from "./ChatRoomBody/ChatBody";
import ChatRoomFooter from "./ChatRoomFooter/ChatRoomFooter";
import ChatRoomHeader from "./ChatRoomHeader/ChatRoomHeader";
import { useAuth } from "../../../../contexts/AuthContextProvider";

type ChatRoomParams = {
  chatRoomId: number;
  isMatched: boolean;
  onClose: () => void;
  onModalAction: (num: number) => void;
  profileImg: string | null;
};

// 필요 데이터
// 행정사
const dummyAgent = {
  agentInfo: {
    agentId: 10,
    name: "엄경례",
    profileImageUrl: "https://placehold.co/748x462",
  },
  strengths: [
    { badgeId: 4, reviewCount: 102 },
    { badgeId: 10, reviewCount: 79 },
    { badgeId: 2, reviewCount: 73 },
    { badgeId: 7, reviewCount: 50 },
    { badgeId: 1, reviewCount: 21 },
    { badgeId: 0, reviewCount: 7 },
  ],
};

// 외국인
const dummyForeigner = {
  basicInfo: {
    foreignerId: 99,
    nickname: "고라니 099",
    nationIdList: [1, 24],
  },
  expectedInfo: {
    targetJob: "웹 개발자",
    companyName: "대박쩌는 IT회사",
    startDate: "2026. 01. 31",
  },
};

const ChatRoom = ({
  chatRoomId,
  isMatched,
  onClose,
  onModalAction,
  profileImg,
}: ChatRoomParams) => {
  const { userType } = useAuth();
  const isAgent = userType === "VALID_AGENT";

  const headerData: ChatRoomHeaderData = isAgent
    ? { type: "FOREIGNER", data: dummyForeigner }
    : { type: "AGENT", data: dummyAgent };

  return (
    <>
      <ChatRoomHeader
        headerData={headerData}
        isMatched={isMatched}
        onClose={onClose}
        onModalAction={onModalAction}
      />
      <ChatBody
        chatRoomId={chatRoomId}
        onModalAction={onModalAction}
        profileImg={profileImg}
        opponentName={
          isAgent
            ? dummyForeigner.basicInfo.nickname
            : dummyAgent.agentInfo.name
        }
        myName={
          isAgent
            ? dummyAgent.agentInfo.name
            : dummyForeigner.basicInfo.nickname
        }
      />
      <ChatRoomFooter isMatched={isMatched} chatRoomId={chatRoomId} />
    </>
  );
};

export default ChatRoom;

import { useContext } from "react";
import type { ChatRoomHeaderData } from "../../../../types/chatRoomTypes";
import ChatBody from "./ChatRoomBody/ChatBody";
import ChatRoomFooter from "./ChatRoomFooter/ChatRoomFooter";
import ChatRoomHeader from "./ChatRoomHeader/ChatRoomHeader";
import { AuthContext } from "../../../../contexts/AuthContext";

type ChatRoomParams = {
  // chatRoomId: number | null;
  // roomStatus: string;
  onClose: () => void;
  onModalAction: (num: number) => void;
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

// 채팅 내역
const dummyData = {
  content: [
    // TEXT인 경우
    {
      messageId: 1025,
      senderId: "MY_ID",
      type: "TEXT",
      content: "안녕하세요, 여권 OCR 데이터 확인했습니다.",
      createdAt: "2026-02-02T06:40:00",
      isRead: true,
    },
    {
      messageId: 1026,
      senderId: "MY_ID",
      type: "TEXT",
      content:
        "안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.",
      createdAt: "2026-02-02T06:40:00",
      isRead: true,
    },
    {
      messageId: 1027,
      senderId: "MY_ID",
      type: "TEXT",
      content:
        "안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.",
      createdAt: "2026-02-02T06:40:00",
      isRead: true,
    },
    {
      messageId: 1028,
      senderId: "MY_ID",
      type: "TEXT",
      content:
        "안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.",
      createdAt: "2026-02-02T06:40:00",
      isRead: true,
    },
    {
      messageId: 1029,
      senderId: "MY_ID",
      type: "TEXT",
      content:
        "안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.",
      createdAt: "2026-02-03T06:40:00",
      isRead: true,
    },
    {
      messageId: 1030,
      senderId: "MY_ID",
      type: "TEXT",
      content: "안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, ",
      createdAt: "2026-02-03T06:40:00",
      isRead: true,
    },
    {
      messageId: 1031,
      senderId: "OPPONENT_ID",
      type: "TEXT",
      content:
        "안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.",
      createdAt: "2026-02-03T06:41:00",
      isRead: true,
    },
    {
      messageId: 1032,
      senderId: "OPPONENT_ID",
      type: "TEXT",
      content:
        "안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.",
      createdAt: "2026-02-03T06:41:00",
      isRead: true,
    },

    // 시스템 메시지인 경우
    {
      messageId: 1033,
      senderId: "OPPONENT_ID",
      type: "PROPOSAL",
      content: "수임 제안서가 도착했습니다.",
      createdAt: "2026-02-03T07:41:00",
      isRead: true,
    },
    {
      messageId: 1034,
      senderId: "OPPONENT_ID",
      type: "ANSWER",
      content: "수임 제안서가 도착했습니다.",
      createdAt: "2026-02-03T06:40:00",
      isRead: true,
    },
  ],
  pageInfo: {
    // 페이징 메타 데이터
    pageNum: 1,
    pageSize: 10,
    totalElements: 100,
    totalPages: 10,
  },
};

const isMatched = true; // 특정 채팅 api에 해당 채팅방의 수임상태가 들어나있나? 이건 부모에서 넘겨받을수 있긴한데 api로 받아오는게 맞지 않나?

const ChatRoom = ({
  /* chatRoomId, roomStatus, */ onClose,
  onModalAction,
}: ChatRoomParams) => {
  const context = useContext(AuthContext);
  if (!context) return null;
  const { userType } = context;
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
        onModalAction={onModalAction}
        chatHistory={dummyData}
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
      <ChatRoomFooter isMatched={isMatched} />
    </>
  );
};

export default ChatRoom;

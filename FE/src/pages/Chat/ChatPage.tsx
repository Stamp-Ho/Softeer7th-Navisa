import { useContext, useState } from "react";
import ChatRoomList from "./components/ChatList/ChatRoomList";
import Envelope from "../../assets/Envelope";
import ChatRoom from "./components/ChatRoom/ChatRoom";
import ChatRoomModal from "./components/Modal/ChatRoomModal";
import ReviewModal from "./components/Review/ReviewModal";
import { AuthContext } from "../../contexts/AuthContext";
import NoChatView from "./components/ChatRoom/NoChatView";
import Tag from "../../components/common/Tag";
import AlarmBadge from "../../assets/icon/AlarmBadge";

type ChatLogData = {
  chatRoomId: number;
  profileImgUrl: string;
  opponentName: string;
  roomStatus: string;
  lastMessage: string;
  noneRead: number;
  lastChattedAt: string;
};

const dummyDataAll: ChatLogData[] = [
  {
    chatRoomId: 0,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-31T16:45:00",
  },
  {
    chatRoomId: 2,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "PROPOSED",
    lastMessage: "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다...",
    noneRead: 0,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 3,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "MATCHED",
    lastMessage: "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다...",
    noneRead: 0,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 4,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 5,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 100,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 6,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 7,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 8,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 9,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 10,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 11,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 12,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 13,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 14,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 15,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 16,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 17,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 18,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 19,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 20,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 21,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 22,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 23,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 24,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 25,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 26,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 27,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 28,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 29,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 30,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 31,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "전체 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
];
const dummyDataUnread: ChatLogData[] = [
  {
    chatRoomId: 0,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-31T16:45:00",
  },
  {
    chatRoomId: 2,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "PROPOSED",
    lastMessage: "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다...",
    noneRead: 0,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 3,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "MATCHED",
    lastMessage: "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다...",
    noneRead: 0,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 4,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 5,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 100,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 6,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 7,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 8,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 9,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 10,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 11,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 12,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 13,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 14,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 15,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 16,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 17,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 18,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 19,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 20,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 21,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 22,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 23,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 24,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 25,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 26,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 27,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 28,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 29,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 30,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 31,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "안 읽은 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
];
const dummyDataMatched: ChatLogData[] = [
  {
    chatRoomId: 0,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-31T16:45:00",
  },
  {
    chatRoomId: 2,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "PROPOSED",
    lastMessage: "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다...",
    noneRead: 0,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 3,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "MATCHED",
    lastMessage: "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다...",
    noneRead: 0,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 4,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 5,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 100,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 6,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 7,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 8,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 9,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 10,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 11,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 12,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 13,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 14,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 15,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 16,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 17,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 18,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 19,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 20,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 21,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 22,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 23,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 24,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 25,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 26,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 27,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 28,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 29,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 30,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
  {
    chatRoomId: 31,
    profileImgUrl: "https://placehold.co/80x80",
    opponentName: "수임 된 채팅방",
    roomStatus: "DEFAULT",
    lastMessage:
      "메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 메시지 보냅니다 ",
    noneRead: 2,
    lastChattedAt: "2026-01-20T16:45:00",
  },
];

// const dummyDataAll: ChatLogData[] = [];
// const dummyDataUnread: ChatLogData[] = [];
// const dummyDataMatched: ChatLogData[] = [];

const ChatPage = () => {
  const [selectedChatRoomId, setSelectedChatRoomId] = useState<number>(-1);
  const [selectedTab, setSelectedTab] = useState<number>(0); // 0:전체 탭, 1:안 읽음 탭, 2:수임 중 탭
  const [viewMessageModal, setViewMessageModal] = useState<number>(0); // 0 미표기, 1 수임 제안하기, 2 수임 취소하기, 3 수임 제안 답변보내기, 4 차단하기
  const [reviewModal, setReviewModal] = useState<number>(0); // 0 미표기, 1 뱃지 리뷰 모달, 2 서비스 리뷰 모달

  const onModalAction = (num: number) => setViewMessageModal(num);
  const onSelectChat = (id: number) => setSelectedChatRoomId(id);
  const onCloseChat = () => setSelectedChatRoomId(-1);
  const reviewHandler = (num: number) => setReviewModal(num);

  // 유저 타입 전역상태 관리
  const context = useContext(AuthContext);
  if (!context) return null;
  const { userType } = context;

  // 본인이 행정사인가?
  const isAgent = userType === "VALID_AGENT";

  // 요건 등록을 했는가?
  const isFileReady =
    userType === "VALID_AGENT" || userType === "FILLED_FOREIGNER";

  // 채팅이 존재하는가?
  const isChatExist = dummyDataAll.length > 0;

  if (!isFileReady || !isChatExist)
    return <NoChatView isFileReady={isFileReady} />;

  const currentTabChatRoomList =
    selectedTab === 0
      ? dummyDataAll
      : selectedTab === 1
        ? dummyDataUnread
        : dummyDataMatched;

  const isCurrentTabChatExist = currentTabChatRoomList.length > 0;

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
        />
      )}

      <div className="relative flex flex-row justify-between mt-12 h-[904px]">
        {/* 상단 채팅 필터링 탭 */}
        <div className="flex flex-col">
          <div className="headline-m-bold text-gray-1000 mb-13">
            상담 메시지
          </div>

          <div className="flex flex-row gap-3 px-3 mb-10">
            <div
              className="cursor-pointer"
              onClick={() => {
                setSelectedTab(0);
                setSelectedChatRoomId(-1);
              }}
            >
              <Tag
                type={
                  selectedTab === 0 ? "large_violet_off" : "large_white_off"
                }
                className="w-[92px]"
              >
                전체
              </Tag>
            </div>
            <div
              className="cursor-pointer"
              onClick={() => {
                setSelectedTab(1);
                setSelectedChatRoomId(-1);
              }}
            >
              <Tag
                type={
                  selectedTab === 1
                    ? "large_violet_on_alarm"
                    : "large_white_on_alarm"
                }
              >
                안 읽음
                {dummyDataUnread.length === 0 || (
                  <AlarmBadge isActive={selectedTab === 1}>
                    {dummyDataUnread.length}
                  </AlarmBadge>
                )}
              </Tag>
            </div>
            {isAgent && (
              <div
                className="cursor-pointer"
                onClick={() => {
                  setSelectedTab(2);
                  setSelectedChatRoomId(-1);
                }}
              >
                <Tag
                  type={
                    selectedTab === 2
                      ? "large_violet_on_alarm"
                      : "large_white_on_alarm"
                  }
                >
                  수임 중
                  {dummyDataMatched.length === 0 || (
                    <AlarmBadge isActive={selectedTab === 2}>
                      {dummyDataMatched.length}
                    </AlarmBadge>
                  )}
                </Tag>
              </div>
            )}
          </div>

          {/* 현재 탭의 채팅방 목록 출력 */}
          {isCurrentTabChatExist && (
            <ChatRoomList
              chatList={currentTabChatRoomList}
              onSelectChat={onSelectChat}
              selectedTab={selectedTab}
              selectedChatRoomId={selectedChatRoomId}
            />
          )}
        </div>

        {isCurrentTabChatExist ? (
          <div className="relative flex flex-col w-[880px] h-full rounded-[20px] overflow-hidden bg-gray-0">
            {/* 선택한 채팅방 없음 */}
            {selectedChatRoomId === -1 && (
              <div className="flex flex-col items-center my-auto">
                <Envelope />
                <div className="mt-7 headline-s-medium text-gray-500">
                  대화할 메시지를 선택해 주세요.
                </div>
              </div>
            )}

            {/* 선택한 채팅방 있음 */}
            {selectedChatRoomId !== -1 && (
              <div className="flex flex-col justify-between h-full">
                <div className="w-full pt-10"></div>
                <ChatRoom
                  // chatRoomId={selectedChatRoomId} // ChatRoom에서 api 호출해서 값을 불러오는데 사용
                  // roomStatus={이거 어떻게?}
                  onClose={onCloseChat}
                  onModalAction={onModalAction}
                />
                <div className="w-full pt-28"></div>
              </div>
            )}
          </div>
        ) : (
          <div className="absolute flex justify-center top-[40%] w-full h-full headline-s-medium text-gray-500">
            읽지 않은 메시지가 없어요.
          </div>
        )}
      </div>

      <button onClick={() => setReviewModal(1)}>리뷰1</button>
      <button onClick={() => setReviewModal(2)}>리뷰2</button>
    </>
  );
};

export default ChatPage;

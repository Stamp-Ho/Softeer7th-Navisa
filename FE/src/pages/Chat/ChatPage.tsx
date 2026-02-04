import { useState } from "react";
import Tag from "../../components/common/Tag";
import ChatRoomList from "./ChatRoomList";
import Envelope from "./Envelope";
import { IcArrows } from "../../assets/icon/StratisUi";
import ChatRoom from "./ChatRoom";
import ChatRoomModal from "./ChatRoomModal";
import AlarmBadge from "../../assets/icon/AlarmBadge";
import ReviewModal from "./ReviewModal";

const dummyDataAll = [
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
const dummyDataUnread = [
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
const dummyDataMatched = [
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
// const dummyDataUnread = [];
// const dummyDataAccpeted = [];
const isFileReady = true;
const amAgent = true;

const ChatPage = () => {
  const [selectedChatRoomId, setSelectedChatRoomId] = useState<number | null>(
    null,
  );
  const [selectedTab, setSelectedTab] = useState<number>(0); // 0:전체 탭, 1:안 읽음 탭, 2:수임 중 탭

  const [viewMessageModal, setViewMessageModal] = useState<number>(0); // 0 미표기, 1 수임 제안하기, 2 수임 취소하기, 3 수임 제안 답변보내기, 4 차단하기

  const [reviewModal, setReviewModal] = useState<number>(0); // 0 미표기, 1 뱃지 리뷰 모달, 2 서비스 리뷰 모달

  const onModalAction = (num: number) => {
    setViewMessageModal(num);
  };

  const onSelectChat = (id: number | null) => {
    setSelectedChatRoomId(id);
  };

  const onCloseChat = () => {
    setSelectedChatRoomId(null);
  };

  const reviewHandler = (num: number) => {
    setReviewModal(num);
  };

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
      {/* 요건 작성 완료 & 채팅 있음 */}
      {isFileReady && dummyDataAll.length > 0 ? (
        <div className="relative flex flex-row justify-between mt-12 h-[904px]">
          <div className="flex flex-col">
            <div className="headline-m-bold text-gray-1000 mb-13">
              상담 메시지
            </div>

            <div className="flex flex-row gap-3 px-3 mb-10">
              <div
                className="cursor-pointer"
                onClick={() => {
                  setSelectedTab(0);
                  setSelectedChatRoomId(null);
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
                  setSelectedChatRoomId(null);
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
              {amAgent && dummyDataMatched.length > 0 && (
                <div
                  className="cursor-pointer"
                  onClick={() => {
                    setSelectedTab(2);
                    setSelectedChatRoomId(null);
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

            {((selectedTab === 0 && dummyDataAll.length > 0) ||
              (selectedTab === 1 && dummyDataUnread.length > 0) ||
              (selectedTab === 2 && dummyDataMatched.length > 0)) && (
              <ChatRoomList
                chatList={
                  selectedTab === 0
                    ? dummyDataAll
                    : selectedTab === 1
                      ? dummyDataUnread
                      : dummyDataMatched
                }
                onSelectChat={onSelectChat}
                selectedTab={selectedTab}
                selectedChatRoomId={selectedChatRoomId}
              />
            )}
          </div>

          {(selectedTab === 0 && dummyDataAll.length > 0) ||
          (selectedTab === 1 && dummyDataUnread.length > 0) ||
          (selectedTab === 2 && dummyDataMatched.length > 0) ? (
            <div className="relative flex flex-col w-[880px] h-full rounded-[20px] overflow-hidden bg-gray-0">
              {/* 선택한 채팅방 없음 */}
              {selectedChatRoomId === null && (
                <div className="flex flex-col items-center my-auto">
                  <Envelope />
                  <div className="mt-7 headline-s-medium text-gray-500">
                    대화할 메시지를 선택해 주세요.
                  </div>
                </div>
              )}

              {/* 선택한 채팅방 있음 */}
              {selectedChatRoomId !== null && (
                <div className="flex flex-col justify-between h-full">
                  <div className="w-full pt-10"></div>
                  <ChatRoom
                    amAgent={amAgent}
                    chatRoomId={selectedChatRoomId}
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
      ) : (
        // 요건 작성 미완료 OR 진행중인 채팅 없음
        <div className="flex flex-col mt-12 h-[904px]">
          <div className="headline-m-bold text-gray-1000 mb-13">
            상담 메시지
          </div>
          <div className="flex flex-col gap-4 justify-center items-center h-full">
            <div className="headline-s-medium text-gray-500">
              진행 중인 상담이 없어요.
            </div>
            <button className="flex flex-row items-center pl-5 title-m-semibold text-violet-500 cursor-pointer">
              {isFileReady ? "행정사 탐색하기" : "내 요건 등록하고 상담하기"}
              <span className="-rotate-90">
                <IcArrows stroke="var(--violet-500)" size={32} />
              </span>
            </button>
          </div>
        </div>
      )}
      <button onClick={() => setReviewModal(1)}>리뷰1</button>
      <button onClick={() => setReviewModal(2)}>리뷰2</button>
    </>
  );
};

export default ChatPage;

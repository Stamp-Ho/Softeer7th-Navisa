import { useRef } from "react";
import Tag from "../../components/common/Tag";
import CalcChattedTime from "../../utils/CalcChattedTme";
import ChatSystemMessage from "./ChatSystemMessage";

type ChatBodyParams = {
  onModalAction: (num: number) => void;
};

type ChatContext = {
  messageId: number;
  senderId: number;
  type: string;
  content: string;
  createdAt: string;
  isRead: boolean;
};

const myId = 5;

const dummyData = {
  isSuccess: true,
  code: 200,
  message: "채팅 내역 조회 성공",
  result: {
    content: [
      // TEXT인 경우
      {
        messageId: 1025,
        senderId: 5,
        type: "TEXT",
        content: "안녕하세요, 여권 OCR 데이터 확인했습니다.",
        createdAt: "2026-02-02T06:40:00",
        isRead: true,
      },
      {
        messageId: 1026,
        senderId: 5,
        type: "TEXT",
        content:
          "안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.",
        createdAt: "2026-02-02T06:40:00",
        isRead: true,
      },
      {
        messageId: 1027,
        senderId: 6,
        type: "TEXT",
        content:
          "안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.",
        createdAt: "2026-02-02T06:40:00",
        isRead: true,
      },
      {
        messageId: 1028,
        senderId: 6,
        type: "TEXT",
        content:
          "안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.",
        createdAt: "2026-02-02T06:40:00",
        isRead: true,
      },
      {
        messageId: 1029,
        senderId: 6,
        type: "TEXT",
        content:
          "안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.",
        createdAt: "2026-02-03T06:40:00",
        isRead: true,
      },
      {
        messageId: 1030,
        senderId: 6,
        type: "TEXT",
        content: "안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, ",
        createdAt: "2026-02-03T06:40:00",
        isRead: true,
      },
      {
        messageId: 1031,
        senderId: 5,
        type: "TEXT",
        content:
          "안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.",
        createdAt: "2026-02-03T06:41:00",
        isRead: true,
      },
      {
        messageId: 1032,
        senderId: 6,
        type: "TEXT",
        content:
          "안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.안녕하세요, 여권 OCR 데이터 확인했습니다.",
        createdAt: "2026-02-03T06:41:00",
        isRead: true,
      },

      // 시스템 메시지인 경우
      {
        messageId: 1033,
        senderId: 5,
        type: "PROPOSAL",
        content: "수임 제안서가 도착했습니다.",
        createdAt: "2026-02-03T07:41:00",
        isRead: true,
      },
      {
        messageId: 1034,
        senderId: 6,
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
  },
};

const ChatBody = ({ onModalAction }: ChatBodyParams) => {
  const scrollRef = useRef<HTMLDivElement>(null);
  const groupedChats = groupChatLogs(dummyData.result.content);

  return (
    <div ref={scrollRef} className="overflow-auto scrollbar-hide">
      {groupedChats.map((group, index) =>
        group[0].type === "DATE" ? (
          <div
            key={group[0].messageId}
            className="flex flex-row justify-center w-full my-10"
          >
            <Tag type="small_fill_gray" className="">
              {CalcDateSystemMessage(group[0].createdAt)}
            </Tag>
          </div>
        ) : (
          <div
            key={index}
            className={`flex flex-col px-6 ${group[0].senderId !== myId ? "items-start" : "items-end"} `}
          >
            <div className="flex flex-row gap-3">
              {group[0].senderId !== myId && (
                <img
                  src="https://placehold.co/56x56"
                  alt="행정사 프로필 사진"
                  className="w-[56px] h-[56px] mr-2 object-cover rounded-full"
                />
              )}

              <div>
                {group.map((chatLog, idx) => (
                  <div
                    key={chatLog.messageId}
                    className={`flex flex-row gap-3 mb-2 ${group[0].senderId === myId ? "justify-end" : "justify-start"}`}
                  >
                    {idx === group.length - 1 && group[0].senderId === myId && (
                      <div className="flex flex-col gap-[2px] justify-end items-end caption-l-regular text-text-sub">
                        <div>{group[group.length - 1].isRead && "읽음"}</div>
                        <div>{CalcChattedTime(group[0].createdAt)}</div>
                      </div>
                    )}

                    {chatLog.type === "TEXT" && (
                      <div
                        className={`max-w-[500px] px-6 py-5 bg-background-sub rounded-b-[10px] ${chatLog.senderId === myId ? "rounded-tl-[10px] rounded-tr-[2px]" : "rounded-tl-[2px] rounded-tr-[10px]"}`}
                      >
                        {chatLog.content}
                      </div>
                    )}
                    {chatLog.type === "PROPOSAL" && (
                      <ChatSystemMessage
                        type="PROPOSAL"
                        onModalAction={onModalAction}
                      />
                    )}
                    {chatLog.type === "ANSWER" && (
                      <ChatSystemMessage
                        type="ANSWER"
                        isAccept={true}
                        onModalAction={onModalAction}
                      />
                    )}

                    {idx === group.length - 1 && (
                      <div className="flex items-end caption-l-regular text-text-sub">
                        {group[0].senderId !== myId &&
                          CalcChattedTime(group[0].createdAt)}
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </div>
            <div className="mb-7"></div>
          </div>
        ),
      )}
    </div>
  );
};

export default ChatBody;

const CalcDateSystemMessage = (isoString: string) => {
  const date = new Date(isoString);

  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const weekDay = date.getDay();
  const week: string[] = ["일", "월", "화", "수", "목", "금", "토"];

  return `${year}년 ${month}월 ${day}일 ${week[weekDay]}요일`;
};

const isSameMinute = (t1: string, t2: string) => {
  return t1.slice(0, 16) === t2.slice(0, 16); // "2026-02-02T06:40"
};
const isDateChanged = (t1: string, t2: string) => {
  return t1.slice(0, 10) !== t2.slice(0, 10);
};

const groupChatLogs = (logs: ChatContext[]) => {
  const groups: ChatContext[][] = [];

  logs.forEach((log, index) => {
    if (index === 0) {
      groups.push([log]);
      return;
    }

    const prev = logs[index - 1];
    const lastGroup = groups[groups.length - 1];

    const sameSender =
      "senderId" in prev && "senderId" in log && prev.senderId === log.senderId;
    const sameMinute = isSameMinute(prev.createdAt, log.createdAt);
    const isNextDay = isDateChanged(prev.createdAt, log.createdAt);

    if (!isNextDay && sameSender && sameMinute) {
      lastGroup.push(log);
    } else {
      if (isNextDay) {
        groups.push([
          {
            messageId: 1000,
            senderId: 5,
            type: "DATE",
            content: "",
            createdAt: log.createdAt,
            isRead: true,
          },
        ]);
      }
      groups.push([log]);
    }
  });
  return groups;
};

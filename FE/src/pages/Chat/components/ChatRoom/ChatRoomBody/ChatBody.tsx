import { useContext, useRef } from "react";
import Tag from "../../../../../components/common/Tag";
import CalcChattedTime from "../../../../../utils/CalcChattedTime";
import ChatSystemMessage from "./ChatSystemMessage";
import { AuthContext } from "../../../../../contexts/AuthContext";
import type {
  ChatLogData,
  ChatMessage,
} from "../../../../../types/chatRoomTypes";

type ChatBodyParams = {
  onModalAction: (num: number) => void;
  chatHistory: ChatLogData;
  opponentName: string;
  myName: string;
};

const ChatBody = ({
  onModalAction,
  chatHistory,
  opponentName,
  myName,
}: ChatBodyParams) => {
  const scrollRef = useRef<HTMLDivElement>(null);
  const groupedChats = groupChatLogs(chatHistory.content);

  const context = useContext(AuthContext);
  if (!context) return null;
  const { userType, userId } = context;
  const isAgent = userType === "VALID_AGENT";
  const myId = userId;

  return (
    <div ref={scrollRef} className="overflow-auto scrollbar-hide">
      {groupedChats.map((group, index) =>
        // 날짜 메시지
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
          // 일반, 시스템 메시지
          <div
            key={index}
            className={`flex flex-col px-6 ${group[0].senderId !== myId ? "items-start" : "items-end"} `}
          >
            <div className="flex flex-row gap-3">
              {group[0].senderId !== myId &&
                (isAgent ? (
                  <div className="flex flex-row justify-center items-center w-[56px] h-[56px] rounded-full bg-violet-25 title-l-bold text-violet-500">
                    {opponentName[0]}
                  </div>
                ) : (
                  <img
                    src="https://placehold.co/56x56"
                    alt="행정사 프로필 사진"
                    className="w-[56px] h-[56px] mr-2 object-cover rounded-full"
                  />
                ))}

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
                        sender={
                          chatLog.senderId === myId ? myName : opponentName
                        }
                      />
                    )}
                    {chatLog.type === "ANSWER" && (
                      <ChatSystemMessage
                        type="ANSWER"
                        isAccept={true}
                        onModalAction={onModalAction}
                      />
                    )}

                    {idx === group.length - 1 && group[0].senderId !== myId && (
                      <div className="flex items-end caption-l-regular text-text-sub">
                        {CalcChattedTime(group[0].createdAt)}
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

const groupChatLogs = (logs: ChatMessage[]) => {
  const groups: ChatMessage[][] = [];

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
            senderId: "SYSTEM",
            type: "DATE",
            content: "SYSTEM_MESSAGE",
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

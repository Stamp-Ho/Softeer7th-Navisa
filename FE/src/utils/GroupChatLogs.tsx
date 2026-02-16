import type { ChatHistoryResponse } from "../api/types/chat";

const isSameMinute = (t1: string, t2: string) => {
  return t1.slice(0, 16) === t2.slice(0, 16);
};
const isDateChanged = (t1: string, t2: string) => {
  return t1.slice(0, 10) !== t2.slice(0, 10);
};

const groupChatLogs = (logs: ChatHistoryResponse[]) => {
  const groups: ChatHistoryResponse[][] = [];

  logs.forEach((log, index) => {
    if (index === 0) {
      groups.push([log]);
      return;
    }

    const prev = logs[index - 1];
    const lastGroup = groups[groups.length - 1];

    const sameSender =
      "isSentByMe" in log &&
      "isSentByMe" in prev &&
      prev.isSentByMe === log.isSentByMe;
    const sameMinute = isSameMinute(prev.createdAt, log.createdAt);
    const isNextDay = isDateChanged(prev.createdAt, log.createdAt);

    if (!isNextDay && sameSender && sameMinute) {
      lastGroup.push(log);
    } else {
      if (isNextDay) {
        groups.push([
          {
            chatMessageId: -new Date(log.createdAt).getTime(),
            isSentByMe: true,
            type: "SYSTEM",
            content: "DATE_REMINDER",
            sentAt: log.createdAt,
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

export default groupChatLogs;

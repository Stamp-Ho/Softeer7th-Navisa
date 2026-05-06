import i18n from "../i18n/config";

const CalcDateSystemMessage = (utcString: string) => {
  if (!utcString) return "";

  // 1. 나노초(9자리) → 밀리초(3자리)로 자르기
  const normalized = utcString.length > 23 ? utcString.slice(0, 23) : utcString;

  // 2. UTC임을 명시하기 위해 Z 붙이기
  const utcDate = new Date(`${normalized}Z`);
  const date = new Date(utcDate);

  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const weekDay = date.getDay();
  const weekDays = i18n.t("utils:dateSystemMessage.weekDays", {
    returnObjects: true,
  }) as Record<string, string>;
  const weekArray = [
    weekDays.sunday || "일",
    weekDays.monday || "월",
    weekDays.tuesday || "화",
    weekDays.wednesday || "수",
    weekDays.thursday || "목",
    weekDays.friday || "금",
    weekDays.saturday || "토",
  ];

  return `${year}년 ${month}월 ${day}일 ${weekArray[weekDay]}요일`;
};

export default CalcDateSystemMessage;

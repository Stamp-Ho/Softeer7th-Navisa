import i18n from "../i18n/config";

const CalcDateSystemMessage = (isoString: string) => {
  const date = new Date(isoString);

  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const weekDay = date.getDay();
  const weekDays = i18n.t("utils:dateSystemMessage.weekDays", { returnObjects: true }) as Record<string, string>;
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

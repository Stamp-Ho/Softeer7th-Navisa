import i18n from "../i18n/config";

const CalcChattedTime = (utcString: string) => {
  if (!utcString) return "";

  // 1. 나노초(9자리) → 밀리초(3자리)로 자르기
  const normalized = utcString.length > 23 ? utcString.slice(0, 23) : utcString;

  // 2. UTC임을 명시하기 위해 Z 붙이기
  const utcDate = new Date(`${normalized}Z`);
  const date = new Date(utcDate);

  const hours = date.getHours();
  const minutes = date.getMinutes().toString().padStart(2, "0");
  const isAM = hours < 12;

  const displayHour = hours % 12 === 0 ? 12 : hours % 12;
  const period = isAM
    ? i18n.t("utils:chattedTime.morning")
    : i18n.t("utils:chattedTime.afternoon");

  return `${period} ${displayHour}:${minutes}`;
};

export default CalcChattedTime;

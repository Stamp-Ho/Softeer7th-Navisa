import i18n from "../i18n/config";

const CalcLastChattedAt = (utcString: string) => {
  if (!utcString) return "";

  // 1. 나노초(9자리) → 밀리초(3자리)로 자르기
  const normalized = utcString.length > 23 ? utcString.slice(0, 23) : utcString;

  // 2. UTC임을 명시하기 위해 Z 붙이기
  const utcDate = new Date(`${normalized}Z`);

  // 3. 로컬 시간으로 자동 변환됨
  const date = new Date(utcDate);
  const now = new Date();

  // 오늘 00:00 (로컬 기준)
  const startOfToday = new Date(
    now.getFullYear(),
    now.getMonth(),
    now.getDate(),
  );

  // 어제 00:00
  const startOfYesterday = new Date(startOfToday);
  startOfYesterday.setDate(startOfToday.getDate() - 1);

  if (date >= startOfToday) {
    // 오늘
    const hours = date.getHours();
    const minutes = date.getMinutes().toString().padStart(2, "0");
    const isAM = hours < 12;

    const displayHour = hours % 12 === 0 ? 12 : hours % 12;
    const period = isAM
      ? i18n.t("utils:lastChattedAt.morning")
      : i18n.t("utils:lastChattedAt.afternoon");

    return `${period} ${displayHour}:${minutes}`;
  }

  if (date >= startOfYesterday) {
    // 어제
    return i18n.t("utils:lastChattedAt.yesterday");
  }

  // 그 외 (로컬 기준 날짜)
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");

  return `${year}.${month}.${day}`;
};

export default CalcLastChattedAt;

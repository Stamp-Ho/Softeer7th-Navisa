import i18n from "../i18n/config";

// D-Day 계산
export const calcDDay = (targetDate: string): string => {
  const today = new Date();
  const normalized = targetDate.replace(/\.\s*/g, "-");
  const target = new Date(normalized);

  // 시/분/초 제거 (날짜 기준으로만 계산)
  today.setHours(0, 0, 0, 0);
  target.setHours(0, 0, 0, 0);

  const diffTime = target.getTime() - today.getTime();
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

  if (diffDays > 0) {
    return i18n.t("utils:dDay.format", { days: diffDays });
  }

  if (diffDays === 0) {
    return i18n.t("utils:dDay.today");
  }

  return i18n.t("utils:dDay.past", { days: Math.abs(diffDays) });
};

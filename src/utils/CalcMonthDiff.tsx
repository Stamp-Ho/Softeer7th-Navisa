import i18n from "../i18n/config";

// 경력 계산
export const calcMonthDiff = (months: number) => {
  if (months <= 12) {
    return i18n.t("utils:monthDiff.months", { months });
  }
  const years = Math.floor(months / 12);
  const remainMonths = months % 12;
  return i18n.t("utils:monthDiff.years", { years, months: remainMonths });
};

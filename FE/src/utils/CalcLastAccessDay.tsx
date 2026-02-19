import i18n from "../i18n/config";

// 마지막 접속일 계산
const CalcLastAccessDay = (lastAccessDay: string) => {
  const lastDate = new Date(lastAccessDay);
  const lastTime = lastDate.getTime();
  const now = new Date();
  if (Number.isNaN(lastTime) || lastTime > now.getTime()) return null;
  const diffMs = now.getTime() - lastDate.getTime();

  const ONE_HOUR = 1000 * 60 * 60;
  const ONE_DAY = ONE_HOUR * 24;

  if (diffMs <= ONE_HOUR) return i18n.t("utils:lastAccessDay.now");
  if (diffMs <= ONE_DAY) return i18n.t("utils:lastAccessDay.within24h");
  if (diffMs <= ONE_DAY * 3) return i18n.t("utils:lastAccessDay.within3d");
  if (diffMs <= ONE_DAY * 7) return i18n.t("utils:lastAccessDay.within7d");
  return null;
};

export default CalcLastAccessDay;

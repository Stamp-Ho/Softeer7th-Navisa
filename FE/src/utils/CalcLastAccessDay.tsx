// 마지막 접속일 계산
const CalcLastAccessDay = (lastAccessDay: string) => {
  const lastDate = new Date(lastAccessDay);
  const lastTime = lastDate.getTime();
  const now = new Date();
  if (Number.isNaN(lastTime) || lastTime > now.getTime()) return null;
  const diffMs = now.getTime() - lastDate.getTime();

  const ONE_HOUR = 1000 * 60 * 60;
  const ONE_DAY = ONE_HOUR * 24;

  if (diffMs <= ONE_HOUR) return "방금 접속했어요.";
  if (diffMs <= ONE_DAY) return "최근 24시간 내 접속";
  if (diffMs <= ONE_DAY * 3) return "최근 3일 이내 접속";
  if (diffMs <= ONE_DAY * 7) return "최근 7일 이내 접속";
  return null;
};

export default CalcLastAccessDay;

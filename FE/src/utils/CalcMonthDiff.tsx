// 경력 계산
export const calcMonthDiff = (months: number) => {
  if (months <= 12) return `${months}개월`;
  const years = Math.floor(months / 12);
  return `${years}년 ${months % 12}개월`;
};

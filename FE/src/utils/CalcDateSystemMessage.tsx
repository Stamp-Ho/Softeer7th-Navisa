const CalcDateSystemMessage = (isoString: string) => {
  const date = new Date(isoString);

  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const weekDay = date.getDay();
  const week: string[] = ["일", "월", "화", "수", "목", "금", "토"];

  return `${year}년 ${month}월 ${day}일 ${week[weekDay]}요일`;
};

export default CalcDateSystemMessage;

const CalcChattedTime = (isoString: string) => {
  const date = new Date(isoString);

  const hours = date.getHours();
  const minutes = date.getMinutes().toString().padStart(2, "0");
  const isAM = hours < 12;

  const displayHour = hours % 12 === 0 ? 12 : hours % 12;

  return `${isAM ? "오전" : "오후"} ${displayHour}:${minutes}`;
};

export default CalcChattedTime;

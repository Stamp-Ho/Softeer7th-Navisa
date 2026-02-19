import i18n from "../i18n/config";

const CalcChattedTime = (isoString: string) => {
  const date = new Date(isoString);

  const hours = date.getHours();
  const minutes = date.getMinutes().toString().padStart(2, "0");
  const isAM = hours < 12;

  const displayHour = hours % 12 === 0 ? 12 : hours % 12;
  const period = isAM ? i18n.t("utils:chattedTime.morning") : i18n.t("utils:chattedTime.afternoon");

  return `${period} ${displayHour}:${minutes}`;
};

export default CalcChattedTime;

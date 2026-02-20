import FormSelector from "../form/inputComponents/FormSelector";
import { useTranslation } from "react-i18next";

const DateSelector = ({
  value = "",
  onChange = (_a: string) => {},
  disabled = false,
}) => {
  const { t } = useTranslation(["common"]);
  // value가 "2024-05-20" 형태라면 분리, 없다면 빈값
  const [year, month, day] =
    value && typeof value === "string" ? value.split("-") : ["", "", ""];
  const today = new Date();
  const thisYear = today.getFullYear();
  const subjectiveYear = thisYear - Number(year);

  const handleDateChange = (type: "Y" | "M" | "D", newValue: number) => {
    // 2. 현재 상태값들을 복사합니다.
    let currentY = year;
    let currentM = month;
    let currentD = day;

    // 3. 변경된 타입만 업데이트합니다.
    if (type === "Y") currentY = String(thisYear - newValue).padStart(2, "0");
    if (type === "M") currentM = String(newValue + 1).padStart(2, "0");
    if (type === "D") currentD = String(newValue + 1).padStart(2, "0");
    onChange(`${currentY}-${currentM}-${currentD}`);
  };

  return (
    <div className="grid grid-cols-3 gap-3">
      <FormSelector
        placeholder={t("datePicker.year")}
        value={year !== "" ? String(subjectiveYear) : ""}
        options={Array.from({ length: 30 }, (_, i) => String(thisYear - i))}
        onChange={(val) => handleDateChange("Y", val)}
        disabled={disabled}
      />
      <FormSelector
        placeholder={t("datePicker.month")}
        value={month !== "" ? String(Number(month) - 1) : ""}
        options={Array.from({ length: 12 }, (_, i) =>
          String(i + 1).padStart(2, "0"),
        )}
        onChange={(val) => handleDateChange("M", val)}
        disabled={disabled}
      />
      <FormSelector
        placeholder={t("datePicker.day")}
        value={day !== "" ? String(Number(day) - 1) : ""}
        options={Array.from(
          {
            length: new Date(Number(year), Number(month), 0).getDate(),
          },
          (_, i) => String(i + 1).padStart(2, "0"),
        )}
        onChange={(val) => handleDateChange("D", val)}
        disabled={disabled}
      />
    </div>
  );
};
export default DateSelector;

import { useEffect, useState } from "react";
import { IcDash } from "../../../assets/icon/StratisUi";
import TimePicker from "../formComponents/TimePicker";

const TimeRangePicker = ({ onChange = (_a: string) => {}, readOnly = false }) => {
  const [startTime, setStartTime] = useState({
    hour: "09",
    minute: "00",
  });
  const [endTime, setEndTime] = useState({
    hour: "18",
    minute: "00",
  });

  useEffect(() => {
    onChange(`${startTime.hour}:${startTime.minute} ~ ${endTime.hour}:${endTime.minute}`);
  }, [startTime, endTime]);
  return (
    <div className="flex flex-row items-center gap-3">
      <TimePicker time={startTime} setTime={setStartTime} readOnly={readOnly} />
      <IcDash size={36} />
      <TimePicker time={endTime} setTime={setEndTime} readOnly={readOnly} />
    </div>
  );
};

export default TimeRangePicker;

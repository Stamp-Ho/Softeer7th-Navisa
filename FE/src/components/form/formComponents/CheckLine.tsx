import { IcCheck } from "../../../assets/icon/StratisUi";

const CheckLine = ({ value = false, setValue = () => {}, label = "", className = "" }) => {
  return (
    <span
      onClick={setValue}
      className={`flex flex-row gap-2 w-full items-center cursor-pointer mb-1.5
        ${value ? "text-primary" : `text-text-sub`}
        ${className}`}
      tabIndex={1}
    >
      <IcCheck activated={value} />
      {label}
    </span>
  );
};

export default CheckLine;

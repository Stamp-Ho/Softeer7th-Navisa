import { IcCheck } from "../../../assets/icon/StratisUi";

const CheckLine = ({ value = false, setValue = () => {}, label = "", className = "" }) => {
  return (
    <a
      onClick={setValue}
      className={`flex flex-row gap-2 w-full items-center cursor-pointer mb-1.5
        ${value ? "text-primary" : `text-text-sub`}
        ${className}`}
    >
      <IcCheck activated={value} />
      {label}
    </a>
  );
};

export default CheckLine;

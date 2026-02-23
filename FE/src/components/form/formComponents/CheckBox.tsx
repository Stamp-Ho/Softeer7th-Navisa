import { IcCheckBroken } from "../../../assets/icon/StratisUi";

const CheckBox = ({ value = false, setValue = (_a: boolean) => {}, label = "", className = "", disabled = false }) => {
  return (
    <a
      onClick={() => !disabled && setValue(!value)}
      className={`flex flex-row gap-2 w-full items-center mb-3
        ${value ? "text-primary" : `text-text-sub`}
        ${className}
        ${disabled ? "" : "cursor-pointer"}`}
    >
      <IcCheckBroken activated={value} />
      {label}
    </a>
  );
};

export default CheckBox;

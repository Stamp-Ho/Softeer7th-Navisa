import { IcCheckBroken } from "../../../assets/icon/StratisUi";

const CheckBox = ({
  value = false,
  setValue = (_a: boolean) => {},
  label = "",
  className = "",
  disabled = false,
  tabIndex = 0,
}) => {
  return (
    <span
      onClick={() => !disabled && setValue(!value)}
      className={`flex flex-row gap-2 w-full items-center mb-3
        ${value ? "text-primary" : `text-text-sub`}
        ${className}
        ${disabled ? "" : "cursor-pointer"}`}
      tabIndex={tabIndex}
    >
      <IcCheckBroken activated={value} />
      {label}
    </span>
  );
};

export default CheckBox;

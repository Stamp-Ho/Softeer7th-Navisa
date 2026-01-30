import { IcCheckBroken } from "../../assets/icon/StratisUi";

const CheckBox = ({
  value = false,
  setValue = () => {},
  label = "",
  className = "",
}) => {
  return (
    <a
      onClick={setValue}
      className={`flex flex-row gap-2 w-full items-center cursor-pointer mb-3
        ${value ? "text-primary" : `text-text-sub`}
        ${className}`}
    >
      <IcCheckBroken activated={value} />
      {label}
    </a>
  );
};

export default CheckBox;

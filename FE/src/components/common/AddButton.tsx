import { IcPlus } from "../../assets/icon/StratisUi";

const AddButton = ({
  className = "",
  onClick = () => {},
  disabled = false,
}) => {
  return (
    <button
      className={`${className} flex-row flex items-center justify-center rounded-lg  w-14 h-14
      ${disabled ? "bg-gray-150" : "bg-violet-50 cursor-pointer"}`}
      onClick={onClick}
      disabled={disabled}
      type="button"
    >
      <IcPlus color={disabled ? "var(--gray-400)" : "#4132A9"} />
    </button>
  );
};
export default AddButton;

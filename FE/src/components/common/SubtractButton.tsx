import { IcDash } from "../../assets/icon/StratisUi";

const SubtractButton = ({ className = "", onClick = () => {} }) => {
  return (
    <button
      className={`${className} cursor-pointer flex-row flex items-center justify-center rounded-lg bg-gray-150 w-14 h-14`}
      onClick={onClick}
    >
      <IcDash color="var(--gray-500)" />
    </button>
  );
};
export default SubtractButton;

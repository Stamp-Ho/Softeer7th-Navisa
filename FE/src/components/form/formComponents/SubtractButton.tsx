import { IcDash } from "../../../assets/icon/StratisUi";

const SubtractButton = ({ className = "", onClick = () => {} }) => {
  return (
    <button
      className={`${className} cursor-pointer flex-row flex items-center justify-center rounded-lg bg-gray-150
        w-14 h-14 focus:outline-violet-100 focus:outline-2`}
      onClick={onClick}
      type="button"
    >
      <IcDash color="var(--gray-500)" />
    </button>
  );
};
export default SubtractButton;

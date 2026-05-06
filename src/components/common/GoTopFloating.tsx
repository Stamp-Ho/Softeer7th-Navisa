import { IcArrowUp } from "../../assets/icon/StratisUi";

const GoTopFloating = ({ onClick = () => {}, className = "" }) => {
  return (
    <button
      className={`rounded-full cursor-pointer shadow bg-white w-16 h-16 flex items-center justify-center
     ${className}`}
      onClick={onClick}
    >
      <IcArrowUp size={20} />
    </button>
  );
};
export default GoTopFloating;

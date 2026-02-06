import { IcPlus } from "../../assets/icon/StratisUi";

const AddButton = ({ className = "", onClick = () => {} }) => {
  return (
    <button
      className={`${className} cursor-pointer flex-row flex items-center justify-center rounded-lg bg-violet-50 w-14 h-14`}
      onClick={onClick}
    >
      <IcPlus />
    </button>
  );
};
export default AddButton;

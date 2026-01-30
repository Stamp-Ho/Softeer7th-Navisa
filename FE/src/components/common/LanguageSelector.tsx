import { useState } from "react";
import FlagIcon from "../../assets/FlagIcon";
import { IcArrows } from "../../assets/icon/StratisUi";

const LanguageSelector = () => {
  const [isOpen, setIsOpen] = useState(false);
  const flagStyle = "w-8 h-8 border border-gray-200 rounded-full";
  return (
    <div className="z-100 border border-gray-200 cursor-pointer rounded-[24px] bg-white h-fit p-2 pr-4 flex flex-col gap-3 ">
      <div
        className="flex flex-row items-center gap-2"
        onClick={() => setIsOpen(!isOpen)}
      >
        <FlagIcon nationIndex={25} className={flagStyle} />
        <div className={`transition-transform ${isOpen ? "rotate-180" : ""}`}>
          <IcArrows />
        </div>
      </div>
      {isOpen && (
        <>
          <FlagIcon nationIndex={7} className={flagStyle} />
          <FlagIcon nationIndex={61} className={flagStyle} />
          <FlagIcon nationIndex={22} className={flagStyle} />
        </>
      )}
    </div>
  );
};

export default LanguageSelector;

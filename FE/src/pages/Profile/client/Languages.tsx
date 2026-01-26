import FlagIcon from "../../../assets/FlagIcon";
import { IcLanguage } from "../../../assets/icon/StratisUi";

const Languages = () => {
  return (
    <div className="flex flex-col gap-5 w-124 px-5 py-7 border border-border-normal rounded-radius-400 bg-white">
      <div className="flex flex-row gap-2 items-center title-m-semibold text-text-base">
        <IcLanguage />
        <span>사용 가능 언어</span>
      </div>
      <ul className="flex flex-row gap-spacing-300">
        <li className="flex flex-row items-center gap-spacing-300 py-spacing-300 pl-spacing-300 pr-spacing-500 bg-green-50 text-green-800 rounded-radius-700 body-l-semibold">
          <FlagIcon nationIndex={25} className="w-8 h-8" />
          <span>한국어</span>
        </li>
      </ul>
    </div>
  );
};

export default Languages;

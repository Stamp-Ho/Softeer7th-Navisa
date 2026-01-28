import FlagIcon from "../../../assets/FlagIcon";
import { IcLanguage } from "../../../assets/icon/StratisUi";
import { languageIconIdxList, languageList } from "../../../types/language";

type LanguageIdList = {
  languageIdList: number[];
};

const Languages = ({ languageIdList }: LanguageIdList) => {
  return (
    <div className="flex flex-col gap-5 w-124 px-5 py-7 border border-border-normal rounded-radius-400 bg-white">
      <div className="flex flex-row gap-2 items-center title-m-semibold text-text-base">
        <IcLanguage />
        <span>사용 가능 언어</span>
        <span className="title-m-semibold text-green-800">
          {languageIdList.length}
        </span>
      </div>
      <ul className="flex flex-row flex-wrap gap-spacing-300">
        {languageIdList.map((id) => (
          <li
            key={id}
            className="flex flex-row items-center gap-spacing-300 py-spacing-300 pl-spacing-300 pr-spacing-500 bg-green-50 text-green-800 rounded-radius-700 body-l-semibold"
          >
            <FlagIcon
              nationIndex={languageIconIdxList[id]}
              className="w-8 h-8"
            />
            <span>{languageList[id]}</span>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Languages;

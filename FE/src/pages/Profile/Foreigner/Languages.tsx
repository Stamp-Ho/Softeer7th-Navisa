import FlagIcon from "../../../assets/FlagIcon";
import { IcLanguage } from "../../../assets/icon/StratisUi";
import { languageIconIdxList, languageList } from "../../../constants/language";
import ProfileItemsFrame from "../../../components/common/ProfileItemsFrame";

const Languages = ({ languageIds = [13, 2] }: { languageIds?: number[] }) => {
  return (
    <ProfileItemsFrame>
      <div className="flex flex-row gap-2 items-center title-m-semibold text-text-base">
        <IcLanguage />
        <span>사용 가능 언어</span>
        <span className="title-m-semibold text-green-800">
          {languageIds.length}
        </span>
      </div>
      <ul className="flex flex-row flex-wrap gap-spacing-300">
        {languageIds.map((id) => (
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
    </ProfileItemsFrame>
  );
};

export default Languages;

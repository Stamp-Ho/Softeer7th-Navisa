import { useTranslation } from "react-i18next";
import FlagIcon from "../../../assets/FlagIcon";
import { IcLanguage } from "../../../assets/icon/StratisUi";
import { languageList } from "../../../constants/language";
import ProfileItemsFrame from "../../../components/common/ProfileItemsFrame";

const Languages = ({ languageIds = [13, 2] }: { languageIds?: number[] }) => {
  const { t } = useTranslation(["pages"]);
  return (
    <ProfileItemsFrame>
      <div className="flex flex-row gap-2 items-center title-m-semibold text-text-base">
        <IcLanguage />
        <span>{t("profile.languages")}</span>
        <span className="title-m-semibold text-green-800">{languageIds.length}</span>
      </div>
      <ul className="flex flex-row flex-wrap gap-spacing-300">
        {languageIds.map((id) => (
          <li
            key={id}
            className="flex flex-row items-center gap-spacing-300 py-spacing-300 pl-2.25 pr-spacing-500 bg-green-50 text-green-800 rounded-full body-l-semibold"
          >
            <FlagIcon nationIndex={id - 1} className="w-8 h-8" />
            <span>{languageList[id - 1]}</span>
          </li>
        ))}
      </ul>
    </ProfileItemsFrame>
  );
};

export default Languages;

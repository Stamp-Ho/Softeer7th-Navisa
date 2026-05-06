import { useTranslation } from "react-i18next";
import FlagIcon from "../../../assets/FlagIcon";
import { IcLanguage } from "../../../assets/icon/StratisUi";
import { languageIconIdxList } from "../../../constants/language";
import ProfileItemsFrame from "../../../components/common/ProfileItemsFrame";
import { useLanguageLabels } from "../../../hooks/useLocalizationLists";

const Languages = ({ languageIds }: { languageIds?: number[] }) => {
  const { t } = useTranslation(["pages"]);
  const languageLabels = useLanguageLabels();
  if (!languageIds || languageIds.length === 0) return <SkeletonLanguages />;
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
            <FlagIcon nationIndex={languageIconIdxList[id - 1]} className="w-8 h-8" />
            <span>{languageLabels[id - 1]}</span>
          </li>
        ))}
      </ul>
    </ProfileItemsFrame>
  );
};

export default Languages;

const SkeletonLanguages = () => {
  const { t } = useTranslation(["pages"]);
  return (
    <ProfileItemsFrame>
      <div className="flex flex-row gap-2 items-center title-m-semibold text-text-base">
        <IcLanguage />
        <span>{t("profile.languages")}</span>
        <span className="title-m-semibold text-green-800 animate-pulse">0</span>
      </div>
      <ul className="flex flex-row flex-wrap gap-spacing-300">
        {[1, 2].map((idx) => (
          <li
            key={idx}
            className="flex flex-row items-center gap-spacing-300 py-spacing-300 pl-2.25 pr-spacing-500 bg-green-50 text-green-800 rounded-full body-l-semibold animate-pulse"
          >
            <div className="w-8 h-8 bg-gray-150 rounded-full" />
            <div className="w-16 h-4 bg-gray-150 rounded-full" />
          </li>
        ))}
      </ul>
    </ProfileItemsFrame>
  );
};

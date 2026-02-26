import FlagIcon from "../../../assets/FlagIcon";
import { IcGraduation, IcLanguage, IcLuggage04 } from "../../../assets/icon/StratisUi";
import Tag from "../../../components/common/Tag";
import { Link } from "react-router-dom";
import type { SearchForeignerCardType } from "../../../types/Cards";
import { useTranslation } from "react-i18next";
import { useLanguageLabels, useDegreeLabelMap } from "../../../hooks/useLocalizationLists";

const SearchForeignerCard = ({
  foreigner,
  withDetails = true,
}: {
  foreigner?: SearchForeignerCardType;
  withDetails?: boolean;
}) => {
  const { t } = useTranslation(["components"]);
  const languageLabels = useLanguageLabels();
  const degreeLabelMap = useDegreeLabelMap();

  if (!foreigner) return SkeletonUi(withDetails);
  return (
    <div
      className={`flex flex-col py-7 px-6 w-92 h-fit rounded-2xl ${withDetails ? " bg-gray-30" : "shadow bg-white"}`}
    >
      <Link to={`/profile/foreigner/${foreigner.foreignerId}`}>
        <ul className="flex flex-row gap-2 items-center">
          {foreigner.nationIdList.slice(0, 5).map((nation) => (
            <FlagIcon key={`flag_${nation - 1}`} nationIndex={nation - 1} className="w-6 h-6" />
          ))}
        </ul>
        <h4 className="mt-2 title-m-bold text-text-base">{foreigner.nickname}</h4>

        <div className="flex flex-row gap-4 mt-4 items-end">
          <div className="flex flex-col gap-3">
            <div className="flex flex-col gap-2">
              <div className="flex flex-row gap-1 items-center w-36 caption-m-medium text-text-base">
                <IcLuggage04 size={14} />
                {t("searchForeignerCard.plannedJob")}
              </div>
              <Tag variant="small_fill_gray_2" className="rounded-sm w-fit">
                {foreigner.jobTitle}
              </Tag>
            </div>
            {withDetails && (
              <div className="flex flex-col w-36 gap-2">
                <div className="flex flex-row gap-1 items-center caption-m-medium text-text-base">
                  <IcGraduation size={14} />
                  {t("searchForeignerCard.finalEducation")}
                </div>
                <Tag variant="small_fill_violet_max" className="w-fit">
                  {degreeLabelMap[foreigner.degreeLevel]}
                </Tag>
              </div>
            )}
          </div>
          <div className="flex flex-col w-36 gap-2">
            <div className="flex flex-row gap-1 items-center caption-m-medium text-text-base">
              <IcLanguage size={14} />
              {t("searchForeignerCard.language")}
            </div>
            <div className="flex flex-row gap-1">
              {foreigner.languageIdList.slice(0, 2).map((lang, idx) => (
                <Tag key={idx} variant="small_fill_green_max" className="w-fit">
                  {languageLabels[lang - 1]}
                </Tag>
              ))}
              {foreigner.languageIdList.length > 2 && (
                <Tag variant="small_fill_gray">+{foreigner.languageIdList.length - 2}</Tag>
              )}
            </div>
          </div>
        </div>
      </Link>
    </div>
  );
};

export default SearchForeignerCard;

const SkeletonUi = (withDetails = true) => {
  return (
    <div className={`flex flex-col py-7 px-6 w-92 h-fit bg-gray-30 rounded-2xl ${withDetails || "shadow bg-white"}`}>
      <ul className="flex flex-row gap-2 items-center">
        <div className="w-6 h-6 rounded-3xl bg-gray-100" />
        <div className="w-6 h-6 rounded-3xl bg-gray-100" />
        <div className="w-6 h-6 rounded-3xl bg-gray-100" />
      </ul>
      <Tag variant="small_fill_gray" className="w-32 mt-2 mb-1" />

      <div className="flex flex-row gap-4 mt-4 items-end">
        <div className="flex flex-col gap-3">
          <div className="flex flex-col gap-2">
            <div className="flex flex-row gap-1 items-center w-36">
              <Tag variant="tiny_skeleton" />
              <Tag variant="tiny_skeleton" className="w-22" />
            </div>
            <Tag variant="small_fill_gray_2" className="rounded-sm w-40" />
          </div>
          {withDetails && (
            <div className="flex flex-col w-36 gap-2">
              <div className="flex flex-row gap-1 items-center">
                <Tag variant="tiny_skeleton" />
                <Tag variant="tiny_skeleton" className="w-13" />
              </div>
              <Tag variant="small_fill_gray" className="w-10" />
            </div>
          )}
        </div>
        <div className="flex flex-col w-36 gap-2">
          <div className="flex flex-row gap-1 items-center">
            <Tag variant="tiny_skeleton" />
            <Tag variant="tiny_skeleton" className="w-13" />
          </div>
          <div className="flex flex-row gap-1">
            <Tag variant="small_fill_gray" className="w-14" />
            <Tag variant="small_fill_gray" className="w-14" />
          </div>
        </div>
      </div>
    </div>
  );
};

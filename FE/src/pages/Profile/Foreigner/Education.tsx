import { useTranslation } from "react-i18next";
import { IcGraduation } from "../../../assets/icon/StratisUi";
import ProfileItemsFrame from "../../../components/common/ProfileItemsFrame";
import Tag from "../../../components/common/Tag";
import { useDegreeLabelMap } from "../../../hooks/useLocalizationLists";
import type { DegreeLevel } from "../../../api/types/common";

const Education = ({ school, degreeLevel, major }: { school?: string; degreeLevel?: string; major?: string }) => {
  const { t } = useTranslation(["pages"]);
  const degreeLabelMap = useDegreeLabelMap();

  if (!school || !degreeLevel || !major) {
    return <SkeletonEducation />;
  }

  return (
    <ProfileItemsFrame>
      <div className="flex flex-row gap-2 title-m-semibold text-text-base ">
        <IcGraduation size={24} />
        <span>{t("profile.education")}</span>
      </div>
      <div className="flex gap-3 items-center">
        <Tag variant="large_violet_off" className="w-fit">
          {degreeLabelMap[degreeLevel as DegreeLevel] || degreeLevel}
        </Tag>
        <span className="title-l-medium text-text-base">{school}</span>
      </div>
      <span className="title-l-medium text-text-base ml-auto -mt-4 mr-7">{major}</span>
    </ProfileItemsFrame>
  );
};
export default Education;

const SkeletonEducation = () => {
  const { t } = useTranslation(["pages"]);
  return (
    <ProfileItemsFrame>
      <div className="flex flex-row gap-2 title-m-semibold text-text-base ">
        <IcGraduation size={24} />
        <span>{t("profile.education")}</span>
      </div>
      <div className="flex gap-3 items-center">
        <Tag variant="large_violet_off" className="w-23 animate-pulse"></Tag>
        <Tag variant="small_fill_gray" className="w-60 animate-pulse" />
      </div>

      <Tag variant="small_fill_gray" className="w-50 ml-auto -mt-4 mr-7 animate-pulse" />
    </ProfileItemsFrame>
  );
};

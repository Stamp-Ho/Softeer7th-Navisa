import { useTranslation } from "react-i18next";
import { IcGraduation } from "../../../assets/icon/StratisUi";
import ProfileItemsFrame from "../../../components/common/ProfileItemsFrame";
import Tag from "../../../components/common/Tag";
import { DegreeToKorean } from "../../../api/types/common";

const Education = ({ school, degreeLevel, major }: { school?: string; degreeLevel?: string; major?: string }) => {
  const { t } = useTranslation(["pages"]);
  if (!school || !degreeLevel || !major) {
    return <SekeltonEducation />;
  }
  return (
    <ProfileItemsFrame>
      <div className="flex flex-row gap-2 title-m-semibold text-text-base ">
        <IcGraduation size={24} />
        <span>{t("profile.education")}</span>
      </div>
      <div className="flex gap-3 items-center">
        <Tag variant="large_violet_off" className="w-23">
          {DegreeToKorean[degreeLevel as keyof typeof DegreeToKorean] || degreeLevel}
        </Tag>
        <span className="title-l-medium text-text-base">{school}</span>
      </div>
      <span className="title-l-medium text-text-base ml-auto -mt-4 mr-7">{major} 전공</span>
    </ProfileItemsFrame>
  );
};
export default Education;

const SekeltonEducation = () => {
  return (
    <ProfileItemsFrame>
      <div className="flex flex-row gap-2 title-m-semibold text-text-base ">
        <IcGraduation size={24} />
        <span>최종 학력</span>
      </div>
      <div className="flex gap-3 items-center">
        <Tag variant="large_violet_off" className="w-23 animate-pulse"></Tag>
        <Tag variant="small_fill_gray" className="w-60 animate-pulse" />
      </div>

      <Tag variant="small_fill_gray" className="w-50 ml-auto -mt-4 mr-7 animate-pulse" />
    </ProfileItemsFrame>
  );
};

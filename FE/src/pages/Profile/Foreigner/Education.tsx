import { useTranslation } from "react-i18next";
import { IcGraduation } from "../../../assets/icon/StratisUi";
import ProfileItemsFrame from "../../../components/common/ProfileItemsFrame";
import Tag from "../../../components/common/Tag";
import { DegreeToKorean } from "../../../api/types/common";

const Education = ({
  school = "으악대학교",
  degreeLevel = "BACHELOR",
  major = "대박전공",
}: {
  school?: string;
  degreeLevel?: string;
  major?: string;
}) => {
  const { t } = useTranslation(["pages"]);
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
        <span className="title-l-medium text-text-base">
          {school} {major} 전공
        </span>
      </div>
    </ProfileItemsFrame>
  );
};
export default Education;

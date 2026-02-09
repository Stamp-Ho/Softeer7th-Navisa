import { IcGraduation } from "../../../assets/icon/StratisUi";
import ProfileItemsFrame from "../../../components/common/ProfileItemsFrame";
import Tag from "../../../components/common/Tag";

const Education = ({
  school = "으악대학교",
  degreeLevel = "석사",
  major = "대박전공",
}: {
  school?: string;
  degreeLevel?: string;
  major?: string;
}) => {
  return (
    <ProfileItemsFrame>
      <div className="flex flex-row gap-2 title-m-semibold text-text-base">
        <IcGraduation size={24} />
        <span>최종학력</span>
      </div>
      <div className="flex gap-3 items-center">
        <Tag type="large_violet_off" className="w-23">
          {degreeLevel}
        </Tag>
        <span className="title-l-medium text-text-base">
          {school} {major} 전공
        </span>
      </div>
    </ProfileItemsFrame>
  );
};
export default Education;

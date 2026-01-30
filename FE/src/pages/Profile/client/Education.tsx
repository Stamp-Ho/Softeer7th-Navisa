import { IcGraduation } from "../../../assets/icon/StratisUi";
import ProfileItemsFrame from "../../../components/common/ProfileItemsFrame";
import Tag from "../../../components/common/Tag";

type LastEducated = {
  schoolName: string;
  degreeLevel: string;
  majorName: string;
};

const Education = ({ schoolName, degreeLevel, majorName }: LastEducated) => {
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
          {schoolName} {majorName} 전공
        </span>
      </div>
    </ProfileItemsFrame>
  );
};
export default Education;

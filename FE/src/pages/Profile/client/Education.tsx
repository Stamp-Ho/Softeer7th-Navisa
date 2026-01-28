import { IcGraduation } from "../../../assets/icon/StratisUi";
import Tag from "../../../components/common/Tag";

type LastEducated = {
  schoolName: string;
  degreeLevel: string;
  majorName: string;
};

const Education = ({ schoolName, degreeLevel, majorName }: LastEducated) => {
  return (
    <div className="flex flex-col gap-5 w-124 px-5 py-7 border border-border-normal rounded-radius-400 bg-white">
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
    </div>
  );
};
export default Education;

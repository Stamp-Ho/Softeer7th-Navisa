import { IcGraduation } from "../../../assets/icon/StratisUi";

const Education = () => {
  return (
    <div className="flex flex-col gap-5 w-124 px-5 py-7 border border-border-normal rounded-radius-400 bg-white">
      <div className="flex flex-row gap-2 title-m-semibold text-text-base">
        <IcGraduation size={24} />
        <span>최종학력</span>
      </div>
      <div className="flex gap-3 items-center">
        <span className="flex justify-center w-23 px-spacing-500 py-spacing-300 rounded-radius-700 bg-violet-50-transpar body-l-semibold">
          학사
        </span>
        <span className="title-l-medium text-text-base">
          으악대학교 대박전공
        </span>
      </div>
    </div>
  );
};
export default Education;

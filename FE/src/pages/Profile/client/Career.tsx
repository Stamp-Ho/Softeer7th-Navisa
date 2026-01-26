import { IcTrendUp } from "../../../assets/icon/StratisUi";

const Career = () => {
  return (
    <div className="flex flex-col gap-5 w-124 px-5 py-7 border border-border-normal rounded-radius-400 bg-white">
      <div className="flex flex-row gap-2 items-center title-m-semibold text-text-base">
        <IcTrendUp />
        <span>경력</span>
      </div>
      <ul className="flex flex-col gap-3">
        <li className="flex flex-row gap-3 items-center">
          <span className="py-spacing-300 px-spacing-500 bg-gray-100 rounded-radius-300 text-text-base body-l-semibold">
            2023. 11. 02 ~ 2024. 11. 02
          </span>
          <span className="text-text-base title-l-medium">
            땡땡회사 머시기 직무
          </span>
          <span className="text-text-sub body-l-medium">12개월</span>
        </li>
      </ul>
    </div>
  );
};

export default Career;

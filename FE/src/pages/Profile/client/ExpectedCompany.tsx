import { IcLuggage04 } from "../../../assets/icon/StratisUi";

const ExpectedCompany = () => {
  return (
    <div className="flex flex-col gap-5 w-124 px-5 py-7 border border-border-normal rounded-radius-400 bg-white">
      <div className="flex flex-row gap-2 items-center title-m-semibold text-text-base">
        <IcLuggage04 />
        <span>입사 예정 정보</span>
      </div>
      <ul className="flex flex-col gap-3">
        <li className="flex flex-row gap-3 items-center">
          <span className="flex justify-center w-23 py-spacing-300 px-spacing-500 text-text-base body-l-semibold bg-gray-100 rounded-radius-300">
            직무
          </span>
          <span className="text-text-base title-l-medium">웹 개발자</span>
        </li>
        <li className="flex flex-row gap-3 items-center">
          <span className="flex justify-center w-23 py-spacing-300 px-spacing-500 text-text-base body-l-semibold bg-gray-100 rounded-radius-300">
            회사명
          </span>
          <span className="text-text-base title-l-medium">대박쩌는 IT회사</span>
        </li>
        <li className="flex flex-row gap-3 items-center">
          <span className="flex justify-center w-23 py-spacing-300 px-spacing-500 text-text-base body-l-semibold bg-gray-100 rounded-radius-300">
            입사 날짜
          </span>
          <span className="text-text-base title-l-medium">2026. 01. 15</span>
        </li>
      </ul>
    </div>
  );
};

export default ExpectedCompany;

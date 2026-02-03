import { useNavigate, useSearchParams } from "react-router-dom";

import FilterWithDropdown from "../../../components/common/FilterWithDropdown";
import {
  IcJob,
  IcLanguage,
  IcLocation,
  IcRotate,
} from "../../../assets/icon/StratisUi";
import { jobList } from "../../../constants/job";
import { regionList } from "../../../constants/regions";
import { languageList } from "../../../constants/language";

const SearchAgentFilter = () => {
  const navigate = useNavigate();
  const [filterParams] = useSearchParams();

  const jobParams = filterParams.getAll("job");
  const regionsParams = filterParams.getAll("region");
  const languageParams = filterParams.getAll("language");

  const validJobIds = jobParams
    .map(Number) // 문자열 배열을 숫자 배열로 변환 (실패 시 NaN)
    .filter(
      (id) => !isNaN(id) && id >= 0 && id < 15, // 직업 15개
    );
  const validRegions = regionsParams.map(Number).filter(
    (id) => !isNaN(id) && id >= 0 && id < 18, // 지역 18개
  );
  const validLanguages = languageParams.map(Number).filter(
    (id) => !isNaN(id) && id >= 0 && id < 16, // 언어 16개
  );

  const initFilter = () => {
    navigate("/search/agent", { replace: true });
  };

  return (
    <div className="flex flex-col items-center w-full">
      <h2 className="headline-m-bold mr-auto text-gray-1000 mb-13 mt-12">
        행정사 탐색
      </h2>
      <div className="flex flex-row items-center w-full gap-3">
        <FilterWithDropdown
          className="flex-5 "
          dropdownOptions={jobList}
          cols={5}
          isActive={validJobIds.length > 0}
        >
          <IcJob isActive={validJobIds.length > 0} />
          <a className="ml-2">
            {validJobIds.length === 0
              ? "직군 선택"
              : validJobIds.length === 1
                ? jobList[validJobIds[0]]
                : `${jobList[validJobIds[0]]} 외 ${validJobIds.length - 1}건`}
          </a>
        </FilterWithDropdown>
        <FilterWithDropdown
          className="flex-2"
          dropdownOptions={regionList}
          isActive={validRegions.length > 0}
        >
          <IcLocation isActive={validRegions.length > 0} />
          <a className="ml-2">
            {validRegions.length === 0
              ? "지역 선택"
              : validRegions.length === 1
                ? regionList[validRegions[0]]
                : `${regionList[validRegions[0]]} 외 ${validRegions.length - 1}건`}
          </a>
        </FilterWithDropdown>
        <FilterWithDropdown
          className="flex-2"
          dropdownOptions={languageList}
          dropdownAlign="right"
          isActive={validLanguages.length > 0}
        >
          <IcLanguage isActive={validLanguages.length > 0} />
          <a className="ml-2">
            {validLanguages.length === 0
              ? "사용 언어 선택"
              : validLanguages.length === 1
                ? languageList[validLanguages[0]]
                : `${languageList[validLanguages[0]]} 외 ${validLanguages.length - 1}건`}
          </a>
        </FilterWithDropdown>
        <div
          className="flex-1 flex-row flex gap-2 items-center justify-center body-l-semibold text-text-base cursor-pointer"
          onClick={initFilter}
        >
          <IcRotate /> 초기화
        </div>
      </div>
    </div>
  );
};

export default SearchAgentFilter;

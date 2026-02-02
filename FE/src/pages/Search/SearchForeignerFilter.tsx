import { useNavigate, useSearchParams } from "react-router-dom";
import FilterWithDropdown from "../../components/common/FilterWithDropdown";
import { jobList } from "../../types/job";
import {
  IcJob,
  IcLanguage,
  IcNationality,
  IcRotate,
} from "../../assets/icon/StratisUi";
import { languageList } from "../../types/language";
import { nationByCategory, nationList } from "../../types/nations";

const SearchforeignerFilter = () => {
  const navigate = useNavigate();
  const [filterParams] = useSearchParams();

  const jobParams = filterParams.getAll("job");
  const nationParams = filterParams.getAll("nation");
  const languageParams = filterParams.getAll("language");

  const validJobIds = jobParams
    .map(Number) // 문자열 배열을 숫자 배열로 변환 (실패 시 NaN)
    .filter(
      (id) => !isNaN(id) && id >= 0 && id < 15, // 직업 15개
    );
  const validNations = nationParams.map(Number).filter(
    (id) => !isNaN(id) && id >= 0 && id < 63, // 국적 63개
  );
  const validLanguages = languageParams.map(Number).filter(
    (id) => !isNaN(id) && id >= 0 && id < 16, // 언어 16개
  );

  const initFilter = () => {
    navigate("/search/foreigner", { replace: true });
  };

  return (
    <div className="flex flex-col items-center w-full">
      <h2 className="headline-m-bold mr-auto text-gray-1000 mb-13 mt-12">
        의뢰인 탐색
      </h2>
      <div className="flex flex-row items-center w-full gap-3">
        <FilterWithDropdown
          className="flex-5"
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
          category={nationByCategory}
          dropdownOptions={nationList}
          isActive={validNations.length > 0}
        >
          <IcNationality />
          <a className="ml-2">
            {validNations.length === 0
              ? "국적 선택"
              : validNations.length === 1
                ? nationList[validNations[0]]
                : `${nationList[validNations[0]]} 외 ${validNations.length - 1}건`}
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

export default SearchforeignerFilter;

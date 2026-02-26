import { useSearchParams } from "react-router-dom";
import { useTranslation } from "react-i18next";

import DropDown from "./Dropdown";

import { useJobListLabels } from "../../assets/JobIcon";
import { regionList } from "../../constants/regions";
import {
  useRegionLabels,
  useNationLabels,
  useLanguageLabels,
} from "../../hooks/useLocalizationLists";
import {
  IcArrows,
  IcJob,
  IcLanguage,
  IcLocation,
  IcNationality,
} from "../../assets/icon/StratisUi";

import type { FilterWithDropdownProps } from "../../types/filterWithDropdownProps";

const FilterWithDropdown = ({
  className,
  category,
  cols,
  isOpen,
  searchAgent,
  paramKey,
  onClick,
  onClose,
}: FilterWithDropdownProps) => {
  const { t } = useTranslation(["components"]);
  const [filterParams] = useSearchParams();
  const jobListLabels = useJobListLabels();
  const regionLabels = useRegionLabels();
  const nationLabels = useNationLabels();
  const languageLabels = useLanguageLabels();

  const thisParams = filterParams.getAll(paramKey);

  const FilterIcon = FILTER_ICONS[paramKey];
  const filterName = t(`filterWithDropdown.${paramKey}`);

  // 각 필터 타입에 따라 라벨 선택
  const getFilterLabels = () => {
    switch (paramKey) {
      case "job":
        return jobListLabels;
      case "region":
        return regionLabels;
      case "nation":
        return nationLabels;
      case "language":
        return languageLabels;
      default:
        return [];
    }
  };

  const filterLabels = getFilterLabels();

  // regionList의 경우 텍스트 값으로 비교, 나머지는 인덱스로 비교
  const selectedIds =
    paramKey === "region"
      ? thisParams
          .map((val) => regionList.indexOf(val))
          .filter((idx) => idx !== -1)
      : thisParams
          .map(Number)
          .filter((id) => !isNaN(id) && id >= 0 && id < filterLabels.length);

  const isActive = selectedIds.length > 0;

  return (
    <div className={`relative ${className}`}>
      <div
        className={`px-7 py-5 border border-border-normal rounded-lg bg-background-default
          flex flex-row items-center body-l-semibold gap-2 cursor-pointer
        ${isActive ? "bg-violet-25 text-primary border-primary" : "text-text-base"}`}
        onClick={onClick}
      >
        <FilterIcon />
        {selectedIds.length === 0
          ? `${filterName}`
          : selectedIds.length === 1
            ? filterLabels[selectedIds[0]]
            : `${filterLabels[selectedIds[0]]} + ${selectedIds.length - 1}`}
        <div className="ml-auto">
          <div className={`transition-transform ${isOpen ? "rotate-180" : ""}`}>
            <IcArrows stroke={isActive ? "var(--primary)" : "#b1b5bc"} />
          </div>
        </div>
      </div>
      {isOpen && (
        <DropDown
          paramKey={paramKey}
          cols={cols}
          category={category}
          searchAgent={searchAgent}
          type={paramKey === "language" ? "right" : "left"}
          onClose={onClose}
        />
      )}
    </div>
  );
};

export default FilterWithDropdown;

const FILTER_ICONS = {
  job: IcJob,
  region: IcLocation,
  language: IcLanguage,
  nation: IcNationality,
};

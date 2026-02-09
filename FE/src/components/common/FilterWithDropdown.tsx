import { useSearchParams } from "react-router-dom";

import DropDown from "./Dropdown";

import { jobList } from "../../constants/job";
import { regionList } from "../../constants/regions";
import { languageList } from "../../constants/language";
import {
  IcArrows,
  IcJob,
  IcLanguage,
  IcLocation,
  IcNationality,
} from "../../assets/icon/StratisUi";

import type { FilterWithDropdownProps } from "../../types/filterWithDropdownProps";
import { nationList } from "../../constants/nations";

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
  const [filterParams] = useSearchParams();
  const thisParams = filterParams.getAll(paramKey);

  const FilterIcon = FILTER_ICONS[paramKey];
  const filterName = FILTER_NAME[paramKey];
  const filterOptions = FILTER_LIST[paramKey];

  const selectedIds = thisParams
    .map(Number)
    .filter((id) => !isNaN(id) && id >= 0 && id < filterOptions.length);

  const isActive = selectedIds.length > 0;

  return (
    <div className={`relative ${className}`}>
      <div
        className={`px-7 py-5 border border-border-normal rounded-lg bg-background-default
          flex flex-row items-center body-l-semibold gap-2 cursor-pointer
        ${isActive ? "bg-violet-25 text-primary border-primary" : "text-text-base"}`}
        onClick={isOpen ? onClose : onClick}
      >
        <FilterIcon />
        {selectedIds.length === 0
          ? `${filterName} 선택`
          : selectedIds.length === 1
            ? filterOptions[selectedIds[0]]
            : `${filterOptions[selectedIds[0]]} 외 ${selectedIds.length - 1}건`}
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

const FILTER_LIST = {
  job: jobList,
  region: regionList,
  language: languageList,
  nation: nationList,
};
const FILTER_ICONS = {
  job: IcJob,
  region: IcLocation,
  language: IcLanguage,
  nation: IcNationality,
};
const FILTER_NAME = {
  job: "직군",
  region: "지역",
  language: "언어",
  nation: "국적",
};

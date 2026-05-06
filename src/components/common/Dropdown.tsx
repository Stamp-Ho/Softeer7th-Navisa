import { useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import Button from "./Button";
import type { DropDownProps } from "../../types/dropdownProps";
import { useNavigate, useSearchParams } from "react-router-dom";
import { alertT } from "../../i18n/alerts";
import { useJobListLabels } from "../../assets/JobIcon";
import { useRegionLabels, useNationLabels, useLanguageLabels } from "../../hooks/useLocalizationLists";

const DropDown = ({ paramKey, type = "left", cols = 1, searchAgent, category, onClose }: DropDownProps) => {
  const { t } = useTranslation(["common"]);
  const jobListLabels = useJobListLabels();
  const regionLabels = useRegionLabels();
  const nationLabels = useNationLabels();
  const languageLabels = useLanguageLabels();

  const navigate = useNavigate();
  const [filterParams] = useSearchParams();
  const [selectedCategoryIdx, setSelectedCategoryIdx] = useState(0);

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

  // regionList의 경우 텍스트 값으로 파싱, 나머지는 인덱스로 파싱
  const thisParams = filterParams
    .getAll(paramKey)
    .map(Number)
    .filter((id) => !isNaN(id) && id >= 0 && id < filterLabels.length);

  const [selectedIds, setSelectedIds] = useState<number[]>(thisParams);

  const onOptionClicked = (targetId: number) => {
    let tempList = [...selectedIds];
    if (tempList.includes(targetId)) {
      tempList = tempList.filter((id) => id !== targetId);
    } else {
      tempList.push(targetId);
    }
    setSelectedIds(tempList);
  };

  const onFilterInit = () => {
    setSelectedIds([]);
  };

  const onOptionInCategoryClicked = (targetId: number) => {
    if (category) {
      const absoluteIndex = filterLabels.indexOf(category[selectedCategoryIdx].items[targetId]);
      onOptionClicked(absoluteIndex);
    }
  };

  const onApply = () => {
    const params = new URLSearchParams(filterParams);

    params.delete(paramKey);
    selectedIds.forEach((v) => {
      params.append(paramKey, String(v));
    });
    navigate(`/search/${searchAgent ? "agent" : "foreigner"}?${params.toString()}`, { replace: true });
    onClose();
  };

  const scrollRef = useRef<HTMLDivElement>(null);
  useEffect(() => {
    const scrollContainer = scrollRef.current;
    if (scrollContainer) {
      const onWheel = (event: WheelEvent) => {
        event.preventDefault();
        if (Math.abs(event.deltaX) > Math.abs(event.deltaY)) scrollContainer.scrollLeft += event.deltaX;
        else scrollContainer.scrollLeft += event.deltaY;
      };

      scrollContainer.addEventListener("wheel", onWheel);
      return () => {
        scrollContainer.removeEventListener("wheel", onWheel);
      };
    }
  }, []);

  const selectedOpts = selectedIds.map((id) => filterLabels[id]);
  const style = type === "left" ? "left-0" : type === "right" ? "right-0" : "";
  const gridStyle = cols === 5 ? `grid-cols-5` : `grid-cols-4`;

  const wrapperRef = useRef<HTMLDivElement>(null);

  const arraysEqual = (a: number[], b: number[]) => a.length === b.length && a.every((v, i) => v === b[i]);

  const closeWithConfirm = () => {
    if (!arraysEqual(thisParams, selectedIds)) alertT("common.dropdown.changesFailed");
    else onClose();
  };

  // 외부 클릭 시 닫기
  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (wrapperRef.current && !wrapperRef.current.contains(e.target as Node)) {
        closeWithConfirm();
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [closeWithConfirm]);

  return (
    <div
      className={`absolute top-20 rounded-xl flex flex-col w-max h-fit whitespace-nowrap
        bg-white z-10 shadow ${style}`}
      ref={wrapperRef}
    >
      <div className="p-9 border-b border-border-normal ">
        {category && (
          <div
            ref={scrollRef}
            className="mb-5 flex max-w-147 flex-row items-center gap-5 overflow-x-auto scrollbar-hide"
          >
            {category.map((cate, idx) => {
              const isSelected = selectedCategoryIdx === idx;
              const hasSelectedOption = category[idx].items.some((opt) => selectedOpts.includes(opt));
              return (
                <button
                  key={cate.name}
                  onClick={() => setSelectedCategoryIdx(idx)}
                  className={`
                    flex h-11 shrink-0 cursor-pointer items-center rounded-full px-4 body-l-semibold transition-colors
                    ${isSelected ? "bg-violet-50-transpar text-violet-500" : "text-text-sub"}
                    ${hasSelectedOption ? "text-violet-500" : ""}
                    `}
                >
                  {cate.name}
                </button>
              );
            })}
          </div>
        )}
        {category ? (
          <div className={`grid ${gridStyle} gap-3`}>
            {category[selectedCategoryIdx]?.items.map((opt, index) => (
              <Button
                key={`filter_btn_with_category_${paramKey}_${index}`}
                onClick={() => onOptionInCategoryClicked(index)}
                variant={selectedOpts.includes(opt) ? "violetLine" : "lightGray"}
                className="w-35 "
              >
                {opt}
              </Button>
            ))}
          </div>
        ) : (
          <div className={`grid ${gridStyle} gap-3 `}>
            {filterLabels.map((opt: string, index: number) => (
              <Button
                key={`filter_btn_${paramKey}_${index}`}
                onClick={() => onOptionClicked(index)}
                variant={selectedIds.includes(index) ? "violetLine" : "lightGray"}
                className="w-35 whitespace-pre-wrap"
              >
                {opt}
              </Button>
            ))}
          </div>
        )}
      </div>
      <div className="flex flex-row ml-auto gap-3 px-9 py-5">
        <Button variant="grayLine" className="w-30" onClick={onFilterInit}>
          {t("button.reset")}
        </Button>
        <Button
          variant="primary"
          className="w-30"
          onClick={() => {
            onApply();
          }}
        >
          {t("button.apply")}
        </Button>
      </div>
    </div>
  );
};

export default DropDown;

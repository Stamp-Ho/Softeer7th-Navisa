import { useEffect, useRef, useState } from "react";
import { IcArrows } from "../../../assets/icon/StratisUi";
import { useFormContext } from "react-hook-form";
import { englishNationList, nationList, nationMap } from "../../../constants/nations";

const FormSelector = ({
  value = "",
  placeholder = "",
  options = [""],
  className = "",
  disabled = false,
  name = "",
  onChange = (_a: number) => {},
  disableTargets = { true: [-1], false: [-1] },
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const [focusedIndex, setFocusedIndex] = useState(-2);
  const [searchText, setSearchText] = useState("");
  const [isNationSelector, setIsNationSelector] = useState(false);
  const { setValue } = useFormContext();
  const selectorRef = useRef<HTMLDivElement>(null);
  const dropdownItemsRef = useRef<(HTMLDivElement | null)[]>([]);

  useEffect(() => {
    if (options.some((opt) => nationList.includes(opt))) {
      setIsNationSelector(true);
    }
  }, [options]);

  // 검색 텍스트로 필터링된 옵션
  const filteredOptions = searchText
    ? isNationSelector
      ? englishNationList
          .filter((opt) => opt.toString().toLowerCase().includes(searchText.toLowerCase()))
          .map((opt) => nationMap[opt])
      : options.filter((opt) => opt.toString().toLowerCase().includes(searchText.toLowerCase()))
    : options;

  // 필터링된 옵션에서 원본 인덱스 매핑
  const filteredIndices = searchText
    ? isNationSelector
      ? englishNationList.reduce((acc: number[], opt, idx) => {
          if (opt.toString().toLowerCase().includes(searchText.toLowerCase())) {
            acc.push(idx);
          }
          return acc;
        }, [])
      : options.reduce((acc: number[], opt, idx) => {
          if (opt.toString().toLowerCase().includes(searchText.toLowerCase())) {
            acc.push(idx);
          }
          return acc;
        }, [])
    : options.map((_, idx) => idx);

  const disableTarget = (index: number) => {
    const parentName = name.split(".")[0];

    if (index > 0) {
      disableTargets.true.forEach((id) => {
        const fieldPath = `${parentName}.sectionData.${id}.disabled`;
        setValue(fieldPath, true); // 값을 true로 명시적 설정
      });
      disableTargets.false.forEach((id) => {
        setValue(`${parentName}.sectionData.${id}.disabled`, false);
      });
    } else {
      disableTargets.true.forEach((id) => {
        setValue(`${parentName}.sectionData.${id}.disabled`, false);
      });
      disableTargets.false.forEach((id) => {
        const fieldPath = `${parentName}.sectionData.${id}.disabled`;
        setValue(fieldPath, true); // 값을 true로 명시적 설정
      });
    }
  };
  // 2. 외부 클릭 감지 로직
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      // 클릭된 엘리먼트가 selectorRef 내부에 포함되어 있지 않다면 닫기
      if (selectorRef.current && !selectorRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };

    if (isOpen) {
      document.addEventListener("mousedown", handleClickOutside);
    }

    return () => {
      // 클린업: 이벤트 리스너 제거
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [isOpen]);

  // focusedIndex 변경 시 해당 항목에 focus
  useEffect(() => {
    if (focusedIndex >= 0 && dropdownItemsRef.current[focusedIndex]) {
      dropdownItemsRef.current[focusedIndex]?.focus();
    } else {
      // focusedIndex가 -1로 초기화될 때 셀렉터에 포커스
      if (focusedIndex === -1 && selectorRef.current) {
        selectorRef.current.focus();
      }
    }
  }, [focusedIndex]);

  const zOfSelector = isOpen ? "z-21" : "";
  const zOfDropdown = isOpen ? "z-20" : "";

  const handleKeyDown = (e: React.KeyboardEvent<HTMLDivElement>) => {
    if (e.key === "Enter" || e.key === " ") {
      e.preventDefault();
      if (!disabled) {
        setIsOpen((prev) => !prev);
        if (!isOpen) setFocusedIndex(0);
      }
    } else if (e.key === "Escape" || (isOpen && e.key === "Tab")) {
      e.preventDefault();
      setIsOpen(false);
      setSearchText("");
      setFocusedIndex(-1);
    } else if (e.key === "ArrowDown") {
      e.preventDefault();
      if (!isOpen) {
        setIsOpen(true);
        setFocusedIndex(0);
      } else {
        if (filteredOptions.length > 0) {
          setFocusedIndex((prev) => (prev + 1) % filteredOptions.length);
        }
      }
    } else if (e.key === "ArrowUp") {
      e.preventDefault();
      if (isOpen) {
        if (filteredOptions.length > 0) {
          setFocusedIndex((prev) => (prev - 1 + filteredOptions.length) % filteredOptions.length);
        }
      }
    } else if (e.key === "Backspace") {
      e.preventDefault();
      setSearchText((prev) => prev.slice(0, -1));
      setIsOpen(true);
      //setFocusedIndex(0);
    } else if (e.key.length === 1 && !e.ctrlKey && !e.metaKey) {
      e.preventDefault();
      const newSearchText = (searchText + e.key).slice(0, 4);
      setSearchText(newSearchText);
      setIsOpen(true);
      setFocusedIndex(-1);
    }
  };

  const handleBlur = (e: React.FocusEvent<HTMLDivElement>) => {
    if (!e.currentTarget.contains(e.relatedTarget as Node)) {
      setIsOpen(false);
      setSearchText("");
      setFocusedIndex(-1);
    }
  };

  const style = disabled
    ? "bg-gray-150"
    : `cursor-pointer ${isOpen && " outline outline-border-normal"}
        ${zOfSelector}
        ${searchText ? "text-text-base bg-white" : value !== "" ? "text-primary outline-violet-100 outline bg-violet-25" : "text-text-base bg-white"}
        ${className}`;
  return (
    <div
      className="relative h-14 focus-within:outline-violet-100 focus-within:outline-3 rounded-xl "
      ref={selectorRef}
      tabIndex={0}
      onKeyDown={handleKeyDown}
      onBlur={handleBlur}
    >
      <div
        className={`absolute left-0 top-0 px-4 py-5 rounded-xl
        flex items-center w-full h-14
        body-l-medium focus:bg-white
        ${style}`}
        onClick={() => disabled || setIsOpen((prev) => !prev)}
      >
        {value === "" || value === undefined || value === null ? (
          <a className="text-text-sub">{placeholder}</a>
        ) : (
          <a className={disabled ? "text-gray-150" : value ? "text-primary" : "text-text-base"}>
            {searchText ? searchText : options[Number(value)]}
          </a>
        )}
        <div className="ml-auto">
          <div className={`transition-transform ${isOpen ? "rotate-180" : ""}`}>
            <IcArrows stroke={value !== "" ? "var(--primary)" : "#b1b5bc"} />
          </div>
        </div>
      </div>

      {/* dropdown */}
      {isOpen && (
        <div
          className={`
            absolute left-0 top-full -mt-2.5 pt-4 pb-1 pr-1
            overflow-y-hidden
            w-full ${filteredOptions.length > 6 ? "h-75" : "h-fit min-h-15"}
            bg-white rounded-lg
            shadow
            rounded-t-none
            ${zOfDropdown}
          `}
        >
          <div className="flex flex-col gap-0.5 overflow-y-auto scrolltrack-hide h-full">
            {filteredOptions.map((opt, filteredIdx) => {
              const originalIdx = filteredIndices[filteredIdx];
              return (
                <div
                  ref={(el) => {
                    dropdownItemsRef.current[filteredIdx] = el;
                  }}
                  className={`py-1.75 flex flex-row items-center pl-5 cursor-pointer hover:bg-violet-50
                  focus:outline-none ${focusedIndex === filteredIdx ? "bg-violet-50 text-primary" : ""}`}
                  onClick={() => {
                    onChange(originalIdx);
                    disableTarget(originalIdx);
                    setIsOpen(false);
                    setSearchText("");
                    setFocusedIndex(-1);
                  }}
                  key={`selector_opt_${opt}`}
                  tabIndex={focusedIndex === filteredIdx ? 0 : -1}
                  onKeyDown={(e) => {
                    if (e.key === "Enter" || e.key === " ") {
                      e.preventDefault();
                      e.stopPropagation();
                      onChange(originalIdx);
                      disableTarget(originalIdx);
                      setIsOpen(false);
                      setSearchText("");
                      setFocusedIndex(-1);
                      setTimeout(() => selectorRef.current?.focus(), 0);
                    } else if (e.key === "Escape" || e.key === "Tab") {
                      e.preventDefault();
                      e.stopPropagation();
                      setIsOpen(false);
                      setSearchText("");
                      setFocusedIndex(-1);
                      setTimeout(() => selectorRef.current?.focus(), 0);
                    } else if (e.key === "ArrowDown") {
                      e.preventDefault();
                      e.stopPropagation();
                      const nextIndex = (filteredIdx + 1) % filteredOptions.length;
                      setFocusedIndex(nextIndex);
                    } else if (e.key === "ArrowUp") {
                      e.preventDefault();
                      e.stopPropagation();
                      const prevIndex = (filteredIdx - 1 + filteredOptions.length) % filteredOptions.length;
                      setFocusedIndex(prevIndex);
                    } else if (e.key === "Backspace") {
                      e.preventDefault();
                      e.stopPropagation();
                      setSearchText((prev) => prev.slice(0, -1));
                    } else if (e.key.length === 1 && !e.ctrlKey && !e.metaKey) {
                      e.preventDefault();
                      e.stopPropagation();
                      setSearchText((prev) => prev + e.key);
                      setFocusedIndex(-1);
                    }
                  }}
                >
                  {opt}
                </div>
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
};

export default FormSelector;

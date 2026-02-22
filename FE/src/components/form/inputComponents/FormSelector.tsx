import { useEffect, useRef, useState } from "react";
import { IcArrows } from "../../../assets/icon/StratisUi";
import { useFormContext } from "react-hook-form";

const FormSelector = ({
  value = "",
  placeholder = "",
  options = [""],
  className = "",
  disabled = false,
  name = "",
  onChange = (_a: number) => {},
  disableTargets = { true: [-1], false: [-1] },
  getMany = false,
  readOnly = false,
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const [focusedIndex, setFocusedIndex] = useState(-2);
  const [searchText, setSearchText] = useState("");
  const { setValue, getValues } = useFormContext();
  const selectorRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);
  const dropdownItemsRef = useRef<(HTMLDivElement | null)[]>([]);

  const parentName = name.slice(0, name.lastIndexOf("."));
  const grandparentName = parentName.slice(0, parentName.lastIndexOf("."));

  const rawValues = getValues(grandparentName);
  const alreadySelectedOption =
    getMany && rawValues ? Object.values(rawValues.map((v: Array<any>) => Object.values(v)[0])) : [];
  rawValues && console.log(rawValues.map((v: Array<any>) => Object.values(v)[0]));
  console.log(alreadySelectedOption);

  // 검색 텍스트로 필터링된 옵션
  const filteredOptions = searchText
    ? options.filter((opt) => opt.toString().toLowerCase().includes(searchText.toLowerCase()))
    : options;

  // 필터링된 옵션에서 원본 인덱스 매핑
  const filteredIndices = searchText
    ? options.reduce((acc: number[], opt, idx) => {
        if (opt.toString().toLowerCase().includes(searchText.toLowerCase())) {
          acc.push(idx);
        }
        return acc;
      }, [])
    : options.map((_, idx) => idx);

  const disableOuterTarget = (index: number) => {
    const sectionIndex = name.split(".")[0];

    if (index > 0) {
      disableTargets.true.forEach((id) => {
        const fieldPath = `${sectionIndex}.sectionData.${id}.disabled`;
        setValue(fieldPath, true); // 값을 true로 명시적 설정
      });
      disableTargets.false.forEach((id) => {
        setValue(`${sectionIndex}.sectionData.${id}.disabled`, false);
      });
    } else {
      disableTargets.true.forEach((id) => {
        setValue(`${sectionIndex}.sectionData.${id}.disabled`, false);
      });
      disableTargets.false.forEach((id) => {
        const fieldPath = `${sectionIndex}.sectionData.${id}.disabled`;
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
      if (focusedIndex === -1 && inputRef.current) {
        inputRef.current.focus();
      }
    }
  }, [focusedIndex]);

  const zOfSelector = isOpen ? "z-[10]" : "";
  const zOfDropdown = isOpen ? "z-[9]" : "";

  const handleKeyDown = (e: React.KeyboardEvent<HTMLDivElement>) => {
    if (e.key === "Enter" || e.key === " ") {
      e.preventDefault();
      if (!disabled && !readOnly) {
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
        ${searchText ? "text-text-base bg-white" : value !== "" && Number(value) >= 0 ? "text-primary placeholder:text-primary outline-violet-100 outline bg-violet-25" : "text-text-base bg-white"}
        ${className}`;
  return (
    <div
      className={`relative h-14 focus-within:outline-violet-100 focus-within:outline-3 rounded-xl ${readOnly ? "pointer-events-none" : ""}`}
      ref={selectorRef}
      onBlur={handleBlur}
    >
      <input
        className={`absolute left-0 top-0 px-4 py-5 rounded-xl
        flex items-center w-full h-14
        body-l-medium focus:bg-white
        ${style}`}
        ref={inputRef}
        tabIndex={0}
        onChange={(e) => {
          const newSearchText = e.target.value;
          setSearchText(newSearchText);
          if (newSearchText) setIsOpen(true);
        }}
        onClick={() => disabled || readOnly || setIsOpen((prev) => !prev)}
        value={searchText}
        placeholder={value !== "" && Number(value) >= 0 ? options[Number(value)] : placeholder}
        onKeyDown={handleKeyDown}
        disabled={disabled || readOnly}
      />
      <div
        className={`absolute right-4 top-1/2 -translate-y-1/2 ${isOpen ? "z-11" : ""}`}
        onClick={() => disabled || readOnly || setIsOpen((prev) => !prev)}
        tabIndex={-1}
      >
        <div className={`transition-transform ${isOpen ? "rotate-180" : ""}`} tabIndex={-1}>
          <IcArrows stroke={value !== "" ? "var(--primary)" : "#b1b5bc"} />
        </div>
      </div>

      {/* dropdown */}
      {isOpen && (
        <div
          className={`
            absolute left-0 top-full -mt-2.5 pt-4 pb-1 pr-1
            overflow-y-hidden
            w-full ${filteredOptions.length > 6 ? "h-75" : "h-fit"}
            bg-white rounded-lg
            shadow
            rounded-t-none
            ${zOfDropdown}
          `}
        >
          <div className="flex flex-col gap-0.5 overflow-y-auto scrolltrack-hide h-full" tabIndex={-1}>
            {filteredOptions.length === 0 && <div className="pt-2 pb-3 text-center text-text-sub">No result</div>}
            {filteredOptions.map((opt, filteredIdx) => {
              const originalIdx = alreadySelectedOption.includes(filteredIndices[filteredIdx])
                ? ("" as any)
                : filteredIndices[filteredIdx];
              const invalid = originalIdx === ("" as any);
              return (
                <div
                  ref={(el) => {
                    dropdownItemsRef.current[filteredIdx] = el;
                  }}
                  className={`py-1.75 flex flex-row items-center pl-5 ${invalid ? "hover:bg-gray-200 cursor-not-allowed" : "hover:bg-violet-50 cursor-pointer"}
                  focus:outline-none ${focusedIndex === filteredIdx ? (invalid ? "bg-gray-200" : "bg-violet-50 text-primary") : ""}
                  `}
                  onClick={() => {
                    if (!invalid) {
                      onChange(originalIdx);
                      disableOuterTarget(originalIdx);
                    } else {
                      onChange("" as any);
                    }
                    setIsOpen(false);
                    setSearchText("");
                    setFocusedIndex(-1);
                  }}
                  key={`selector_opt_${opt}`}
                  tabIndex={focusedIndex === filteredIdx ? 0 : -1}
                  onKeyDown={(e) => {
                    if (e.key === "Enter" || e.key === " " || e.key === "Tab") {
                      e.preventDefault();
                      e.stopPropagation();
                      if (!invalid) {
                        onChange(originalIdx);
                        disableOuterTarget(originalIdx);
                      } else {
                        onChange("" as any);
                      }
                      setIsOpen(false);
                      setSearchText("");
                      setFocusedIndex(-1);
                      setTimeout(() => inputRef.current?.focus(), 0);
                    } else if (e.key === "Escape") {
                      e.preventDefault();
                      e.stopPropagation();
                      setIsOpen(false);
                      setSearchText("");
                      setFocusedIndex(-1);
                      setTimeout(() => inputRef.current?.focus(), 0);
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

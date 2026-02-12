import { useEffect, useRef, useState } from "react";
import { IcArrows } from "../../assets/icon/StratisUi";
import { useFormContext } from "react-hook-form";

const Selector = ({
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
  const { unregister, setValue } = useFormContext();
  const selectorRef = useRef<HTMLDivElement>(null); // 1. ref 생성

  const disableTarget = (index: number) => {
    const parentName = name.split(".")[0];

    if (index > 0) {
      disableTargets.true.forEach((id) => {
        const fieldPath = `${parentName}.${id}.disabled`;
        setValue(fieldPath, true); // 값을 true로 명시적 설정
      });
      disableTargets.false.forEach((id) => {
        unregister(`${parentName}.${id}.disabled`);
      });
    } else {
      disableTargets.true.forEach((id) => {
        unregister(`${parentName}.${id}.disabled`);
      });
      disableTargets.false.forEach((id) => {
        const fieldPath = `${parentName}.${id}.disabled`;
        setValue(fieldPath, true); // 값을 true로 명시적 설정
      });
    }
  };
  // 2. 외부 클릭 감지 로직
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      // 클릭된 엘리먼트가 selectorRef 내부에 포함되어 있지 않다면 닫기
      if (
        selectorRef.current &&
        !selectorRef.current.contains(event.target as Node)
      ) {
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

  const zOfSelector = isOpen ? "z-21" : "";
  const zOfDropdown = isOpen ? "z-20" : "";

  const style = disabled
    ? "bg-gray-150"
    : `cursor-pointer ${isOpen && " outline outline-border-normal"}
        ${zOfSelector}
        ${value !== "" ? "bg-violet-25 text-primary outline-primary" : "bg-background-default text-text-base"}
        ${className}`;
  return (
    <div className="relative h-14" ref={selectorRef}>
      <div
        className={`absolute left-0 top-0 px-4 py-5 rounded-lg
        flex items-center w-full h-14
        body-l-medium
        ${style}`}
        onClick={() => disabled || setIsOpen((prev) => !prev)}
      >
        {value === "" ? (
          <a className="text-text-sub">{placeholder}</a>
        ) : (
          <a className={disabled ? "text-gray-150" : "text-text-base"}>
            {options[Number(value)]}
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
            w-full ${options.length > 6 ? "h-75" : "h-fit"}
            bg-white rounded-lg
            shadow
            rounded-t-none
            ${zOfDropdown}
          `}
        >
          <div className="flex flex-col gap-0.5 overflow-y-auto scrolltrack-hide h-full">
            {options.map((opt, index) => (
              <div
                className="py-1.75 flex flex-row items-center pl-5 cursor-pointer hover:bg-gray-50"
                onClick={() => {
                  onChange(index);
                  disableTarget(index);
                  setIsOpen(false);
                }}
                key={`selector_opt_${index}`}
              >
                {opt}
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default Selector;

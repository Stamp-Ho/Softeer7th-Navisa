import { useFormContext } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { useRef, useState } from "react";

const FormRadio = ({
  value = -1,
  name = "",
  onChange = (_a: number) => {},
  setValue = (..._event: any[]) => {},
  className = "",
  options = [""] as any[],
  disableNextField = false,
}) => {
  const { t } = useTranslation(["common"]);
  const { toggleDisableNextField } = useToggleDisableNextField();
  const isBoolean = options[0] === false;
  const radioItemsRef = useRef<(HTMLDivElement | null)[]>([]);

  const handleOnClick = (index: number, opt?: string) => {
    //4-1 현재 혼인 사항만을 위한 기능
    if (disableNextField) {
      const slicedName = name.split(".");
      const nextFieldDisableLabel = `${slicedName[0]}.sectionData.${Number(slicedName[2]) + 1}.disabled`;

      if (index > 0) toggleDisableNextField(nextFieldDisableLabel, true);
      else toggleDisableNextField(nextFieldDisableLabel, false);
    }

    if (isBoolean) setValue(opt);
    else onChange(index);
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLDivElement>, index: number) => {
    if (e.key === "ArrowRight" || e.key === "ArrowDown") {
      e.preventDefault();
      const nextIndex = (index + 1) % options.length;
      radioItemsRef.current[nextIndex]?.focus();
    } else if (e.key === "ArrowLeft" || e.key === "ArrowUp") {
      e.preventDefault();
      const prevIndex = (index - 1 + options.length) % options.length;
      radioItemsRef.current[prevIndex]?.focus();
    } else if (e.key === " " || e.key === "Enter") {
      e.preventDefault();
      handleOnClick(index, options[index]);
    }
  };
  const boolToLabel = (bool: boolean) => {
    return bool ? t("radio.yes") : t("radio.no");
  };
  return (
    <div className={`h-14 flex flex-row ${className}`}>
      {options.map((opt, index) => (
        <div
          ref={(el) => {
            radioItemsRef.current[index] = el;
          }}
          key={`radio_${index}`}
          className={`${index === 0 ? "rounded-l-[10px] " : index === options.length - 1 ? "rounded-r-[10px] -ml-px" : "-ml-px"}
            body-l-medium outline cursor-pointer
            w-full flex items-center justify-center
            ${index === value || value === opt ? "outline-violet-200 bg-violet-25 text-primary z-0" : "text-text-sub  bg-white border-border-normal"}
            focus:outline-none focus:ring-2 focus:ring-violet-100 focus:ring-offset-1 focus:z-10
            focus:text-primary
          `}
          onClick={() => {
            handleOnClick(index, opt);
          }}
          onKeyDown={(e) => handleKeyDown(e, index)}
          tabIndex={index === 0 ? 0 : -1}
        >
          {isBoolean ? boolToLabel(!opt) : opt}
        </div>
      ))}
    </div>
  );
};
export default FormRadio;

const useToggleDisableNextField = () => {
  const { setValue } = useFormContext();
  const toggleDisableNextField = (label: string, bool: boolean) => setValue(label, bool);
  return { toggleDisableNextField };
};

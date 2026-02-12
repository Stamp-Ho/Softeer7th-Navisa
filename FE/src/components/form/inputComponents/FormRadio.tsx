import { useFormContext } from "react-hook-form";

const FormRadio = ({
  value = -1,
  name = "",
  onChange = (_a: number) => {},
  setValue = (..._event: any[]) => {},
  className = "",
  options = [""] as any[],
  disableNextField = false,
}) => {
  const { toggleDisableNextField } = useToggleDisableNextField();
  const isBoolean = options[0] === false;

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
  const boolToKorean = (bool: boolean) => {
    return bool ? "예" : "아니오";
  };
  return (
    <div className={`h-14 flex flex-row ${className}`}>
      {options.map((opt, index) => (
        <div
          key={`radio_${index}`}
          className={`${index === 0 ? "rounded-l-[10px] " : index === options.length - 1 ? "rounded-r-[10px] -ml-px" : "-ml-px"}
            body-l-medium border cursor-pointer
            w-full flex items-center justify-center
            ${index === value || value === opt ? "border-violet-200 bg-violet-50 text-primary z-0" : "text-text-sub  bg-white border-border-normal"}
          `}
          onClick={() => {
            handleOnClick(index, opt);
          }}
        >
          {isBoolean ? boolToKorean(!opt) : opt}
        </div>
      ))}
    </div>
  );
};
export default FormRadio;

const useToggleDisableNextField = () => {
  const { setValue } = useFormContext();
  const toggleDisableNextField = (label: string, bool: boolean) =>
    setValue(label, bool);
  return { toggleDisableNextField };
};

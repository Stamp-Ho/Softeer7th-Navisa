import { Controller, useFormContext, useWatch } from "react-hook-form";
import type { input } from "../../types/formType";
import Radio from "../common/Radio";
import Selector from "../common/Selector";
import DateSelector from "../common/DateSelector";
import { useEffect } from "react";
import TimeRangePicker from "./inputComponents/TimeRangePicker";

const InputRenderer = ({
  input,
  inputLabel,
  maxLength = 50,
  className,
}: {
  input: input;
  inputLabel: string;
  maxLength?: number;
  className?: string;
}) => {
  const { register, unregister, control, setValue, getValues } =
    useFormContext();
  const fieldLabels = inputLabel.split(".");

  const isInputDisabled = useWatch({
    control,
    name: `${inputLabel}disabled` || "is.disabled", // 체크박스의 name
  });
  const isFieldDisabled = useWatch({
    control,
    name: `${fieldLabels[0]}.${fieldLabels[1]}.disabled`,
  });
  useEffect(() => {
    // 현재 값이 없을 때만 초기값 설정 (기존 값을 덮어쓰지 않기 위함)
    const currentValue = getValues(inputLabel);
    if (currentValue === undefined) {
      // 렌더링 직후 즉시 빈 문자열로 초기화
      setValue(inputLabel, "", { shouldValidate: false });
    }
  }, [inputLabel, setValue, getValues]);
  const isDisabled = isInputDisabled || isFieldDisabled;

  useEffect(() => {
    if (isDisabled) {
      // 체크박스가 선택되면 날짜 필드 값을 초기화
      unregister(inputLabel);
    }
  }, [isDisabled, inputLabel]);

  const isInputRequired = input.isRequired && !isDisabled;

  switch (input.inputType) {
    case "text":
      return (
        <input
          className={`w-full px-5 rounded-xl
          text-[16px] font-medium focus:outline-gray-300 focus:outline-2 h-14
          ${className}
          ${isDisabled ? "placeholder:text-gray-300 bg-gray-150 text-gray-150 " : "placeholder:text-gray-400 bg-white"}`}
          placeholder={input.placeholder}
          disabled={isDisabled}
          type="text"
          {...register(inputLabel ?? "noLabel", {
            required: isInputRequired,
            maxLength: maxLength,
          })}
        />
      );
    case "selector":
      return (
        <Controller
          name={inputLabel ?? "noLabel"}
          control={control}
          rules={{ required: isInputRequired }}
          render={({ field }) => (
            <Selector
              {...field}
              options={input.options}
              placeholder={input.placeholder}
              disableTargets={input.disableTargets ?? { true: [], false: [] }}
            />
          )}
        />
      );
    case "radio":
      return (
        <Controller
          name={inputLabel ?? "noLabel"}
          control={control}
          rules={{ required: isInputRequired }}
          render={({ field }) => (
            <Radio {...field} options={input.options} className={className} />
          )}
        />
      );
    case "date":
      return (
        <Controller
          name={inputLabel ?? "noLabel"}
          control={control}
          rules={{ required: isInputRequired }}
          render={({ field }) => (
            <DateSelector {...field} disabled={isDisabled} />
          )}
        />
      );
    case "textArea":
      return (
        <textarea
          className={`w-full px-spacing-600 py-spacing-600 bg-white rounded-radius-400 min-h-40 body-l-medium
              focus:outline-gray-300 focus:outline-2 
              placeholder:text-text-sub resize-none ${className}`}
          placeholder={input.placeholder}
          {...register(inputLabel ?? "noLabel")}
        />
      );
    case "timeRange":
      return (
        <Controller
          name={inputLabel ?? "noLabelTimeRange"}
          control={control}
          rules={{ required: isInputRequired }}
          render={({ field }) => <TimeRangePicker {...field} />}
        />
      );
    // ... 나머지 케이스
    default:
      return null;
  }
};
export default InputRenderer;

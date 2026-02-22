import { Controller, useFormContext, useWatch } from "react-hook-form";
import { VALIDATOR, type input } from "../../types/formType";
import DateSelector from "../common/DateSelector";
import { useEffect, useCallback } from "react";
import TimeRangePicker from "./inputComponents/TimeRangePicker";
import FormRadio from "./inputComponents/FormRadio";
import FormSelector from "./inputComponents/FormSelector";

const getNestedError = (errors: any, path: string) => {
  return path.split(".").reduce((obj, key) => obj?.[key], errors);
};

const InputRenderer = ({
  input,
  inputLabel,
  className,
  getMany = false,
  readOnly = false,
}: {
  input: input;
  inputLabel: string;
  className?: string;
  getMany?: boolean;
  readOnly?: boolean;
}) => {
  const {
    control,
    setValue,
    getValues,
    trigger,
    formState: { errors },
  } = useFormContext();
  const fieldError = getNestedError(errors, inputLabel);
  const fieldLabels = inputLabel.split(".");

  const isInputDisabled = useWatch({
    control,
    name: `${inputLabel}disabled`,
  });
  const isFieldDisabled = useWatch({
    control,
    name: `${fieldLabels[0]}.sectionData.${fieldLabels[2]}.disabled`,
  });

  const isDisabled = isInputDisabled || isFieldDisabled;

  useEffect(() => {
    // 현재 값이 없을 때만 초기값 설정 (기존 값을 덮어쓰지 않기 위함)
    if (isDisabled) {
      setValue(inputLabel, undefined, { shouldValidate: false });
    } else {
      const currentValue = getValues(inputLabel);
      if (currentValue === undefined) {
        // 렌더링 직후 즉시 빈 문자열로 초기화

        setValue(inputLabel, "", { shouldValidate: false });
      }
    }
  }, [inputLabel, setValue, getValues, isDisabled]);

  useEffect(() => {
    if (isDisabled) {
      setValue(inputLabel, undefined, { shouldValidate: false });
    }
  }, [isDisabled, inputLabel, setValue]);

  const isInputRequired = input.isRequired && !isDisabled;
  const validator = input.validator ?? "none";

  const validatePattern = useCallback(
    (value: string) => {
      if (!value) return true; // 빈 값은 required 규칙으로 처리
      return VALIDATOR[validator].test(value) || `${input.inputDescription}의 형식이 올바르지 않습니다.`;
    },
    [validator, input.inputDescription],
  );

  const validateDate = (value: string) => {
    if (!value || (!input.onlyPast && !input.onlyFuture)) return true; // 날짜 검증이 필요 없는 경우

    const validatePast = !!input.onlyPast;
    const validateFuture = !!input.onlyFuture;

    const selectedDate = new Date(value);
    const today = new Date();
    today.setHours(0, 0, 0, 0); // 시간 제거하여 날짜만 비교

    // 특정 필드에서만 과거 날짜 검증
    // 예: 생년월일은 과거만, 예상 입사일은 미래만
    if (validatePast) {
      // 과거 날짜만 허용
      return selectedDate <= today || "해당 필드는 오늘 이전이어야 합니다.";
    }

    if (validateFuture) {
      // 오늘과 미래 날짜만 허용
      return selectedDate >= today || "해당 필드는 오늘 이후여야 합니다.";
    }

    return true; // 다른 필드는 검증 안함
  };

  switch (input.inputType) {
    case "text":
      return (
        <>
          <Controller
            name={inputLabel ?? "noLabel"}
            control={control}
            rules={{
              required: isInputRequired,
              validate: validatePattern,
            }}
            render={({ field }) => (
              <input
                {...field}
                onBlur={() => {
                  field.onBlur();
                  trigger(inputLabel);
                }}
                className={`w-full px-5 rounded-xl
                text-[16px] font-medium focus:outline-violet-100 focus:outline-2 focus:text-text-base focus:bg-white h-14
                ${className}
                ${
                  isDisabled
                    ? "placeholder:text-gray-300 bg-gray-150 text-gray-150 "
                    : fieldError
                      ? "outline-2 outline-red-400 text-red-400 bg-white"
                      : field.value
                        ? "text-primary outline-violet-100 outline bg-violet-25"
                        : "placeholder:text-gray-400 text-text-base bg-white"
                }`}
                placeholder={input.placeholder}
                disabled={isDisabled || readOnly}
                type="text"
                maxLength={input.maxLength ?? 50}
                tabIndex={0}
              />
            )}
          />
          {fieldError && <p className="caption-m-medium text-red-400 h-0 mt-1 -mb-1 pl-5">형식이 올바르지 않습니다</p>}
        </>
      );
    case "selector":
      return (
        <Controller
          name={inputLabel ?? "noLabel"}
          control={control}
          rules={{ required: isInputRequired }}
          render={({ field }) => (
            <FormSelector
              {...field}
              options={input.options}
              placeholder={input.placeholder}
              disabled={isDisabled}
              readOnly={readOnly}
              disableTargets={input.disableTargets ?? { true: [], false: [] }}
              getMany={getMany}
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
            <FormRadio
              {...field}
              options={input.options}
              className={className}
              disableNextField={input.disableNextField}
              readOnly={readOnly}
            />
          )}
        />
      );
    case "date":
      return (
        <>
          <Controller
            name={inputLabel ?? "noLabel"}
            control={control}
            rules={{ required: isInputRequired, validate: validateDate }}
            render={({ field }) => (
              <DateSelector
                {...field}
                disabled={isDisabled}
                onlyFuture={input.onlyFuture}
                onlyPast={input.onlyPast}
                isBirthDate={input.birthDate}
                readOnly={readOnly}
              />
            )}
          />
          {fieldError && (
            <p className="caption-m-medium text-red-400 h-0 mt-1 -mb-1 pl-4">
              {fieldError.type === "required"
                ? "필수 항목입니다."
                : input.onlyFuture
                  ? "해당 필드는 오늘보다 이후여야 합니다."
                  : "해당 필드는 오늘보다 이전이어야 합니다."}
            </p>
          )}
        </>
      );
    case "textArea":
      return (
        <Controller
          name={inputLabel ?? "noLabel"}
          control={control}
          rules={{ required: isInputRequired }}
          render={({ field }) => (
            <textarea
              {...field}
              className={`w-full px-spacing-600 py-spacing-600 bg-white rounded-radius-400 min-h-40 body-l-medium
              focus:outline-gray-300 focus:outline-2 
              placeholder:text-text-sub resize-none ${className}`}
              placeholder={input.placeholder}
              disabled={isDisabled || readOnly}
              maxLength={input.maxLength ?? 500}
            />
          )}
        />
      );
    case "timeRange":
      return (
        <Controller
          name={inputLabel ?? "noLabelTimeRange"}
          control={control}
          rules={{ required: isInputRequired }}
          render={({ field }) => <TimeRangePicker {...field} readOnly={readOnly} />}
        />
      );
    // ... 나머지 케이스
    default:
      return null;
  }
};
export default InputRenderer;
